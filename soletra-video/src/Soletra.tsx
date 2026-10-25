// Spelling-race meme video (pt-BR): word on top, leaderboard rows slide in while a voice reads each answer,
// wrong answers in red with an X, meme reactions (cube turn to a b/w cartoon face) and cartoon explosions.
// Everything is driven by src/timeline.json, written by scripts/gerar_vozes.py (same timings as voice and SFX).
import React from 'react';
import {
	AbsoluteFill, Audio, Easing, Sequence, continueRender, delayRender, interpolate, random, spring, staticFile,
	useCurrentFrame, useVideoConfig,
} from 'remotion';
import tl from './timeline.json';

type Linha = {texto: string; tempo: number | null; certo: boolean; frame: number; vo: string; voDur: number};
type Rodada = {
	palavra: string; reacao: string | null; inicio: number; dur: number; linhas: Linha[];
	reacaoIni: number; saidaIni: number;
};

const W = 1080;
const H = 1920;
const FONTE = 'Fredoka';
const TITULO_Y = 440;
const LINHA_Y0 = 530;
const LINHA_PASSO = 140;
const LINHA_H = 124;
const LINHA_W = 990;
const LINHA_X = (W - LINHA_W) / 2;

const VERDE = '#7CF58A';
const VERMELHO = '#FF4258';
const AMARELO = '#FFD84A';

// ---------------------------------------------------------------- font (offline, no google-fonts package)
const fonteHandle = delayRender('fonte');
new FontFace(FONTE, `url(${staticFile('fonts/Fredoka.ttf')})`, {weight: '300 700'})
	.load()
	.then((f) => {
		document.fonts.add(f);
		continueRender(fonteHandle);
	});

const clamp = {extrapolateLeft: 'clamp', extrapolateRight: 'clamp'} as const;

// spring that always arrives from fully outside and never overshoots twice (editor rule: enter from the edge)
const ENTRA = {stiffness: 300, damping: 26};
const SAI = {stiffness: 240, damping: 30};

const contorno = (px: number, cor = '#14161b'): React.CSSProperties => ({
	WebkitTextStroke: `${px}px ${cor}`,
	paintOrder: 'stroke fill',
});

// horizontal motion blur that follows velocity, region much larger than the element (no visible crop line)
const BlurX: React.FC<{id: string; v: number; children: React.ReactNode; style?: React.CSSProperties}> = ({id, v, children, style}) => {
	const s = Math.min(28, Math.abs(v) * 0.22);
	return (
		<>
			<svg width={0} height={0} style={{position: 'absolute'}}>
				<filter id={id} x={-3000} y={-3000} width={9000} height={9000} filterUnits="userSpaceOnUse">
					<feGaussianBlur stdDeviation={`${s} 0`} />
				</filter>
			</svg>
			<div style={{...style, filter: s > 0.4 ? `url(#${id})` : undefined}}>{children}</div>
		</>
	);
};

// ---------------------------------------------------------------- sky (game-like background)
const Nuvem: React.FC<{x: number; y: number; s: number; o: number}> = ({x, y, s, o}) => (
	<div style={{position: 'absolute', left: x, top: y, width: 420 * s, height: 160 * s, opacity: o}}>
		{[[0, 60, 170], [110, 0, 220], [250, 40, 170], [60, 70, 300]].map(([l, t, d], i) => (
			<div key={i} style={{
				position: 'absolute', left: l * s, top: t * s, width: d * s, height: d * s * 0.62, borderRadius: '50%',
				background: 'rgba(255,255,255,0.75)',
			}} />
		))}
	</div>
);

const Ceu: React.FC<{g: number}> = ({g}) => {
	const nuvens = [
		{x: -80, y: 110, s: 1.3, o: 0.55, v: 0.25},
		{x: 560, y: 40, s: 1.0, o: 0.5, v: 0.18},
		{x: 250, y: 1500, s: 1.6, o: 0.35, v: 0.3},
		{x: 700, y: 1250, s: 1.1, o: 0.3, v: 0.22},
		{x: -200, y: 1720, s: 1.4, o: 0.35, v: 0.2},
	];
	return (
		<AbsoluteFill style={{background: 'linear-gradient(180deg, #49CBEA 0%, #8EE3F5 45%, #C9F3FB 100%)'}}>
			{nuvens.map((n, i) => {
				const x = ((n.x + g * n.v + 500) % (W + 900)) - 500;
				return <Nuvem key={i} x={x} y={n.y + Math.sin(g / 40 + i) * 6} s={n.s} o={n.o} />;
			})}
			{/* soft hill at the bottom, blurred like the out-of-focus game world */}
			<div style={{
				position: 'absolute', left: -300, right: -300, top: 1560, height: 900, borderRadius: '50%',
				background: 'linear-gradient(180deg, #6FD86A, #3FAE4F)', filter: 'blur(6px)', opacity: 0.9,
			}} />
			<div style={{position: 'absolute', right: 52, top: 92, fontFamily: FONTE, fontWeight: 700, fontSize: 46, letterSpacing: 1}}>
				<span style={{color: '#D9FAFF'}}>SOLETRA </span><span style={{color: '#FFFFFF'}}>AÍ</span>
			</div>
		</AbsoluteFill>
	);
};

// ---------------------------------------------------------------- original blocky avatar (not a game asset)
const PELES = ['#F6C9A0', '#E0A877', '#B97B4F', '#8A5634', '#FFD35A', '#F2B9A0'];
const CORES = ['#E8453C', '#3C7BE8', '#2EBD67', '#9B4DE8', '#F28C28', '#222831', '#E84393', '#00B3B3'];

const Avatar: React.FC<{seed: string; size: number}> = ({seed, size}) => {
	const r = (k: string) => random(`${seed}-${k}`);
	const pele = PELES[Math.floor(r('p') * PELES.length)];
	const cor = CORES[Math.floor(r('c') * CORES.length)];
	const tipo = Math.floor(r('t') * 5);
	const oculos = r('o') > 0.7;
	return (
		<svg width={size} height={size} viewBox="0 0 100 100">
			<circle cx={50} cy={50} r={48} fill="#3a3f49" stroke="#14161b" strokeWidth={4} />
			<rect x={27} y={30} width={46} height={44} rx={9} fill={pele} stroke="#14161b" strokeWidth={2.5} />
			{tipo === 0 && <><rect x={24} y={22} width={52} height={16} rx={6} fill={cor} /><rect x={40} y={32} width={42} height={7} rx={3} fill={cor} /></>}
			{tipo === 1 && <path d="M25 40 Q25 18 50 18 Q75 18 75 40 L68 34 L60 38 L52 31 L44 38 L36 32 Z" fill={cor === '#FFD35A' ? '#222' : '#3b2618'} />}
			{tipo === 2 && <path d="M24 36 Q50 4 76 36 Z" fill={cor} stroke="#14161b" strokeWidth={2} />}
			{tipo === 3 && <path d="M30 30 L34 14 L42 26 L50 10 L58 26 L66 14 L70 30 Z" fill="#FFC92E" stroke="#14161b" strokeWidth={2} />}
			{tipo === 4 && <rect x={25} y={26} width={50} height={12} rx={5} fill="#f4f4f4" />}
			{oculos ? (
				<><rect x={32} y={44} width={15} height={9} rx={2} fill="#111" /><rect x={53} y={44} width={15} height={9} rx={2} fill="#111" /><rect x={46} y={46} width={8} height={3} fill="#111" /></>
			) : (
				<><rect x={37} y={45} width={6} height={9} rx={2} fill="#14161b" /><rect x={57} y={45} width={6} height={9} rx={2} fill="#14161b" /></>
			)}
			<path d="M38 62 Q50 71 62 62" stroke="#14161b" strokeWidth={3.5} fill="none" strokeLinecap="round" />
		</svg>
	);
};

// ---------------------------------------------------------------- leaderboard row
const tamanhoTexto = (t: string) => Math.min(76, 680 / (t.length * 0.56));

const ehCta = (t: string) => t.startsWith('Comenta');

const SetaBaixo: React.FC = () => (
	<svg width={70} height={70} viewBox="0 0 70 70">
		<path d="M35 8 V52 M15 34 L35 56 L55 34" stroke={AMARELO} strokeWidth={10} fill="none" strokeLinecap="round" strokeLinejoin="round" />
	</svg>
);

const Linha: React.FC<{l: Linha; i: number; lf: number; r: Rodada; seed: string}> = ({l, i, lf, r, seed}) => {
	const {fps} = useVideoConfig();
	const xEntra = (f: number) => interpolate(spring({frame: f - l.frame, fps, config: ENTRA}), [0, 1], [-1180, 0]);
	const saidaQ = r.saidaIni + i * 2;
	const xSai = (f: number) => interpolate(spring({frame: f - saidaQ, fps, config: SAI}), [0, 1], [0, 1250]);
	const xDe = (f: number) => xEntra(f) + (r.reacao ? 0 : xSai(f));
	let x = xDe(lf);
	const v = x - xDe(lf - 1);
	const pouso = l.frame + 7;
	// wrong answer: short horizontal shake on landing; right answer: little pop
	if (!l.certo && !ehCta(l.texto)) x += Math.sin((lf - pouso) * 2.4) * 14 * interpolate(lf - pouso, [0, 14], [1, 0], clamp) * (lf >= pouso ? 1 : 0);
	const pop = l.certo ? 1 + 0.045 * Math.sin(interpolate(lf - pouso, [0, 8], [0, Math.PI], clamp)) : 1;

	// explosion: rows are blown off-frame
	let ex = 0, ey = 0, rot = 0;
	if (r.reacao === 'explosao' && lf >= r.reacaoIni + 2) {
		const t = (lf - (r.reacaoIni + 2)) / fps;
		const ang = (random(`${seed}-a`) - 0.5) * 1.4 + (i % 2 ? 0.25 : -0.25);
		const sp = 2400 + random(`${seed}-s`) * 1600;
		const dir = random(`${seed}-d`) > 0.5 ? 1 : -1;
		ex = Math.sin(ang) * sp * t + dir * 900 * t;
		ey = -Math.cos(ang) * sp * 0.55 * t + 2600 * t * t;
		rot = dir * (220 + random(`${seed}-r`) * 300) * t;
	}

	const y = LINHA_Y0 + i * LINHA_PASSO;
	const cor = ehCta(l.texto) ? AMARELO : l.certo ? VERDE : VERMELHO;
	return (
		<BlurX id={`b-${seed}`} v={v} style={{
			position: 'absolute', left: LINHA_X, top: y, width: LINHA_W, height: LINHA_H,
			transform: `translate(${x + ex}px, ${ey}px) rotate(${rot}deg) scale(${pop})`,
		}}>
			<div style={{
				width: '100%', height: '100%', borderRadius: LINHA_H / 2, background: 'linear-gradient(180deg,#3b404b,#2a2e36)',
				border: '6px solid #14161b', boxSizing: 'border-box', display: 'flex', alignItems: 'center', paddingLeft: 8, paddingRight: 34,
				boxShadow: '0 8px 0 rgba(0,0,0,0.22)',
			}}>
				<Avatar seed={seed} size={102} />
				<div style={{
					flex: 1, marginLeft: 22, fontFamily: FONTE, fontWeight: 700, fontSize: tamanhoTexto(l.texto), color: cor,
					whiteSpace: 'nowrap', lineHeight: 1, ...contorno(9),
				}}>{l.texto}</div>
				{l.tempo !== null && (
					<div style={{fontFamily: FONTE, fontWeight: 700, fontSize: 52, color: '#E6FFD9', ...contorno(7)}}>{l.tempo.toFixed(1)}s</div>
				)}
				{!l.certo && !ehCta(l.texto) && (
					<div style={{fontFamily: FONTE, fontWeight: 700, fontSize: 64, color: VERMELHO, ...contorno(8)}}>X</div>
				)}
				{ehCta(l.texto) && <SetaBaixo />}
			</div>
		</BlurX>
	);
};

// ---------------------------------------------------------------- title word
const Titulo: React.FC<{texto: string; lf: number; r: Rodada}> = ({texto, lf, r}) => {
	const {fps} = useVideoConfig();
	const yDe = (f: number) => {
		const e = interpolate(spring({frame: f, fps, config: ENTRA}), [0, 1], [-420, 0]);
		const s = r.reacao ? 0 : interpolate(spring({frame: f - r.saidaIni, fps, config: SAI}), [0, 1], [0, -620]);
		return e + s;
	};
	const y = yDe(lf);
	const v = y - yDe(lf - 1);
	const fs = Math.min(112, 980 / (texto.length * 0.6));
	let ex = 0, ey = 0;
	if (r.reacao === 'explosao' && lf >= r.reacaoIni + 2) {
		const t = (lf - r.reacaoIni - 2) / fps;
		ey = -2600 * t + 1800 * t * t;
		ex = 300 * t;
	}
	return (
		<>
			<svg width={0} height={0} style={{position: 'absolute'}}>
				<filter id="tituloBlur" x={-3000} y={-3000} width={9000} height={9000} filterUnits="userSpaceOnUse">
					<feGaussianBlur stdDeviation={`0 ${Math.min(24, Math.abs(v) * 0.2)}`} />
				</filter>
			</svg>
			<div style={{
				position: 'absolute', left: 0, width: W, top: TITULO_Y - fs * 0.6, textAlign: 'center',
				transform: `translate(${ex}px, ${y + ey}px)`, filter: Math.abs(v) > 2 ? 'url(#tituloBlur)' : undefined,
				fontFamily: FONTE, fontWeight: 700, fontSize: fs, color: '#FFFFFF', lineHeight: 1.1, ...contorno(12),
			}}>{texto}</div>
		</>
	);
};

// ---------------------------------------------------------------- meme reactions (original cartoon faces)
const Rosto: React.FC<{tipo: string; lf: number}> = ({tipo, lf}) => {
	const pisca = tipo === 'olhar' ? interpolate(lf % 40, [0, 18, 24, 40], [-6, 10, 10, -6], clamp) : 0;
	const lagrima = (lf * 9) % 260;
	return (
		<svg width={900} height={900} viewBox="0 0 400 400">
			<defs>
				<radialGradient id="pele" cx="40%" cy="35%" r="70%">
					<stop offset="0" stopColor="#ffe37a" /><stop offset="1" stopColor="#f0a92a" />
				</radialGradient>
			</defs>
			<circle cx={200} cy={210} r={160} fill="url(#pele)" stroke="#1b1b1b" strokeWidth={8} />
			{tipo === 'choque' && (
				<>
					<ellipse cx={140} cy={175} rx={42} ry={56} fill="#fff" stroke="#1b1b1b" strokeWidth={6} />
					<ellipse cx={260} cy={175} rx={42} ry={56} fill="#fff" stroke="#1b1b1b" strokeWidth={6} />
					<circle cx={140} cy={180} r={10} fill="#1b1b1b" /><circle cx={260} cy={180} r={10} fill="#1b1b1b" />
					<path d="M100 110 Q140 85 175 108" stroke="#1b1b1b" strokeWidth={9} fill="none" strokeLinecap="round" />
					<path d="M225 108 Q260 85 300 110" stroke="#1b1b1b" strokeWidth={9} fill="none" strokeLinecap="round" />
					<ellipse cx={200} cy={290} rx={34} ry={46} fill="#5a1a12" stroke="#1b1b1b" strokeWidth={6} />
					{/* hands on the mouth */}
					<path d="M120 300 Q150 250 200 268 Q250 250 280 300 Q290 345 200 352 Q110 345 120 300 Z" fill="#f0a92a" stroke="#1b1b1b" strokeWidth={6} />
					<path d="M160 290 L165 335 M190 282 L192 340 M220 282 L218 340 M248 290 L242 335" stroke="#1b1b1b" strokeWidth={4} />
				</>
			)}
			{tipo === 'olhar' && (
				<>
					<path d="M95 165 Q140 140 185 165 Q140 200 95 165 Z" fill="#fff" stroke="#1b1b1b" strokeWidth={6} />
					<path d="M215 165 Q260 140 305 165 Q260 200 215 165 Z" fill="#fff" stroke="#1b1b1b" strokeWidth={6} />
					<circle cx={168 + pisca} cy={167} r={13} fill="#1b1b1b" /><circle cx={288 + pisca} cy={167} r={13} fill="#1b1b1b" />
					<path d="M95 130 L180 120" stroke="#1b1b1b" strokeWidth={10} strokeLinecap="round" />
					<path d="M220 108 Q260 92 305 118" stroke="#1b1b1b" strokeWidth={10} fill="none" strokeLinecap="round" />
					<path d="M150 290 L265 278" stroke="#1b1b1b" strokeWidth={10} strokeLinecap="round" />
				</>
			)}
			{tipo === 'choro' && (
				<>
					<path d="M100 175 Q140 150 180 175" stroke="#1b1b1b" strokeWidth={10} fill="none" strokeLinecap="round" />
					<path d="M220 175 Q260 150 300 175" stroke="#1b1b1b" strokeWidth={10} fill="none" strokeLinecap="round" />
					<path d="M105 120 Q140 135 175 128" stroke="#1b1b1b" strokeWidth={8} fill="none" strokeLinecap="round" />
					<path d="M225 128 Q260 135 295 120" stroke="#1b1b1b" strokeWidth={8} fill="none" strokeLinecap="round" />
					<path d="M140 320 Q170 270 200 290 Q230 270 260 320 Z" fill="#5a1a12" stroke="#1b1b1b" strokeWidth={6} />
					{[125, 275].map((cx) => (
						<g key={cx}>
							<rect x={cx - 16} y={180} width={32} height={200} rx={16} fill="#7fd0ff" opacity={0.9} />
							<circle cx={cx} cy={185 + lagrima % 200} r={10} fill="#fff" opacity={0.8} />
						</g>
					))}
				</>
			)}
		</svg>
	);
};

const Reacao: React.FC<{tipo: string; lf: number; g: number}> = ({tipo, lf, g}) => {
	const punch = interpolate(lf, [0, 10], [1.22, 1], {...clamp, easing: Easing.bezier(0.16, 1, 0.3, 1)});
	const push = 1 + lf * 0.0022;
	return (
		<AbsoluteFill style={{background: 'radial-gradient(circle at 50% 45%, #6b6b6b, #121212 75%)', filter: 'grayscale(1) contrast(1.25)'}}>
			<AbsoluteFill style={{alignItems: 'center', justifyContent: 'center', transform: `scale(${punch * push}) translateY(${Math.sin(g / 3) * 3}px)`}}>
				<Rosto tipo={tipo} lf={lf} />
			</AbsoluteFill>
			<AbsoluteFill style={{background: 'radial-gradient(circle, transparent 45%, rgba(0,0,0,0.75) 100%)'}} />
		</AbsoluteFill>
	);
};

// ---------------------------------------------------------------- cartoon explosion (spiky burst layers + smoke puffs)
const estrela = (cx: number, cy: number, raio: number, pontas: number, seed: string, rot: number) => {
	const pts: string[] = [];
	for (let k = 0; k < pontas * 2; k++) {
		const a = (k / (pontas * 2)) * Math.PI * 2 + rot;
		const rr = k % 2 === 0 ? raio * (0.85 + random(`${seed}${k}`) * 0.35) : raio * (0.5 + random(`${seed}i${k}`) * 0.12);
		pts.push(`${cx + Math.cos(a) * rr},${cy + Math.sin(a) * rr}`);
	}
	return pts.join(' ');
};

const Explosao: React.FC<{lf: number}> = ({lf}) => {
	const t = lf;
	const cx = W / 2;
	const cy = 860;
	const cresce = (ate: number, max: number) => interpolate(t, [0, ate], [0.15, 1], {...clamp, easing: Easing.bezier(0.16, 1, 0.3, 1)}) * max;
	const fogo = interpolate(t, [10, 20], [1, 0], clamp);
	const flash = interpolate(t, [0, 1, 4], [0, 1, 0], clamp);
	const puffs = Array.from({length: 14}, (_, i) => i);
	return (
		<AbsoluteFill>
			<svg width={W} height={H} style={{position: 'absolute'}}>
				{/* smoke: puffs drift outward and fade */}
				{puffs.map((i) => {
					const a = (i / puffs.length) * Math.PI * 2 + random(`pa${i}`) * 0.4;
					const d = interpolate(t, [4, 34], [120, 420 + random(`pd${i}`) * 200], {...clamp, easing: Easing.bezier(0.16, 1, 0.3, 1)});
					const rr = interpolate(t, [4, 34], [90, 190 + random(`pr${i}`) * 80], clamp);
					const o = interpolate(t, [6, 12, 34], [0, 0.9, 0], clamp);
					return <circle key={i} cx={cx + Math.cos(a) * d} cy={cy + Math.sin(a) * d * 1.1 - t * 3} r={rr} fill={i % 2 ? '#6a6a6a' : '#8a8a8a'} opacity={o} />;
				})}
				<g opacity={fogo}>
					<polygon points={estrela(cx, cy, cresce(8, 640), 14, 'e1', t * 0.02)} fill="#C8321A" stroke="#3a0d05" strokeWidth={10} strokeLinejoin="round" />
					<polygon points={estrela(cx, cy, cresce(7, 500), 12, 'e2', -t * 0.03)} fill="#FF7A1F" />
					<polygon points={estrela(cx, cy, cresce(6, 360), 10, 'e3', t * 0.04)} fill="#FFC02E" />
					<polygon points={estrela(cx, cy, cresce(5, 210), 9, 'e4', 0.3)} fill="#FFF6B0" />
				</g>
			</svg>
			<AbsoluteFill style={{background: '#fff', opacity: flash}} />
		</AbsoluteFill>
	);
};

// ---------------------------------------------------------------- round
const Rodada: React.FC<{r: Rodada; idx: number}> = ({r, idx}) => {
	const lf = useCurrentFrame();
	const g = lf + r.inicio;
	const quadro = (
		<AbsoluteFill style={{transform: `scale(${1 + lf * 0.00012})`}}>
			<Titulo texto={r.palavra} lf={lf} r={r} />
			{r.linhas.map((l, i) => lf >= l.frame - 1 && (
				<Linha key={i} l={l} i={i} lf={lf} r={r} seed={`${idx}-${i}`} />
			))}
		</AbsoluteFill>
	);

	if (r.reacao && r.reacao !== 'explosao') {
		// cube turn: game face out to the left, reaction face in from the right; same again on the way out
		const p1 = interpolate(lf, [r.reacaoIni, r.reacaoIni + 9], [0, 1], {...clamp, easing: Easing.bezier(0.83, 0, 0.17, 1)});
		const p2 = interpolate(lf, [r.saidaIni, r.saidaIni + 10], [0, 1], {...clamp, easing: Easing.bezier(0.83, 0, 0.17, 1)});
		const sai = (p: number): React.CSSProperties => ({transformOrigin: '100% 50%', transform: `translateX(${-W * p}px) rotateY(${-90 * p}deg)`, backfaceVisibility: 'hidden'});
		const entra = (p: number): React.CSSProperties => ({transformOrigin: '0% 50%', transform: `translateX(${W * (1 - p)}px) rotateY(${90 * (1 - p)}deg)`, backfaceVisibility: 'hidden'});
		return (
			<AbsoluteFill style={{background: '#000', perspective: 2200}}>
				{p1 < 1 && <AbsoluteFill style={sai(p1)}><Ceu g={g} />{quadro}</AbsoluteFill>}
				{p1 > 0 && p2 < 1 && <AbsoluteFill style={p2 > 0 ? sai(p2) : entra(p1)}><Reacao tipo={r.reacao} lf={lf - r.reacaoIni} g={g} /></AbsoluteFill>}
				{p2 > 0 && <AbsoluteFill style={entra(p2)}><Ceu g={g} /></AbsoluteFill>}
			</AbsoluteFill>
		);
	}

	let shake = '';
	if (r.reacao === 'explosao') {
		const k = lf - r.reacaoIni;
		const amp = k >= 0 ? interpolate(k, [0, 22], [26, 0], clamp) : 0;
		shake = `translate(${(random(`sx${g}`) - 0.5) * 2 * amp}px, ${(random(`sy${g}`) - 0.5) * 2 * amp}px)`;
	}
	return (
		<AbsoluteFill style={{transform: `${shake} scale(${r.reacao === 'explosao' ? 1.06 : 1})`}}>
			<Ceu g={g} />
			{quadro}
			{r.reacao === 'explosao' && lf >= r.reacaoIni && <Explosao lf={lf - r.reacaoIni} />}
		</AbsoluteFill>
	);
};

// ---------------------------------------------------------------- film grain on top
const Grao: React.FC = () => {
	const f = useCurrentFrame();
	return (
		<AbsoluteFill style={{opacity: 0.07, mixBlendMode: 'overlay', pointerEvents: 'none'}}>
			<svg width={W} height={H}>
				<filter id="grao"><feTurbulence type="fractalNoise" baseFrequency="0.9" numOctaves={2} seed={f % 12} /></filter>
				<rect width={W} height={H} filter="url(#grao)" />
			</svg>
		</AbsoluteFill>
	);
};

export const Soletra: React.FC<{semAudio?: boolean}> = ({semAudio}) => {
	const rodadas = tl.rodadas as Rodada[];
	const fim = tl.final as Rodada;
	return (
		<AbsoluteFill style={{background: '#000'}}>
			<Sequence durationInFrames={rodadas[0].inicio}><Ceu g={0} /></Sequence>
			{rodadas.map((r, i) => (
				<Sequence key={i} from={r.inicio} durationInFrames={r.dur}><Rodada r={r} idx={i} /></Sequence>
			))}
			<Sequence from={fim.inicio}><Rodada r={fim} idx={99} /></Sequence>
			<Grao />
			{!semAudio && <Audio src={staticFile('audio/mix.wav')} />}
		</AbsoluteFill>
	);
};
