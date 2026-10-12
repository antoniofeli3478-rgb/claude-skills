#!/usr/bin/env node
// Executa a matriz do benchmark (Playwright + CDP) e grava uma linha JSON por execução em resultados/brutos.jsonl.
// Retomável: pula execuções já gravadas (mesmo id + rep). Uso:
//   node rodar.js --perfil=smoke|principal|d1x|d6x|m4x|carga|ablacao[,outro] [--so=regex] [--reps=N] [--saida=arq.jsonl]
// Ambiente: CHROMIUM_PATH=/caminho/do/chrome (padrão: /opt/pw-browsers/chromium-1194/chrome-linux/chrome)
const fs = require('fs'), path = require('path'), os = require('os');
const { iniciar } = require('./servidor');
const req = m => { try { return require(m); } catch (e) { for (const d of ['/opt/node22/lib/node_modules', '/opt/node-tools/node_modules']) { try { return require(path.join(d, m)); } catch (e2) {} } throw e; } };
const { chromium } = req('playwright');
const arg = (k, d) => { const a = process.argv.find(x => x.startsWith('--' + k + '=')); return a ? a.split('=').slice(1).join('=') : d; };
const EXE = process.env.CHROMIUM_PATH || '/opt/pw-browsers/chromium-1194/chrome-linux/chrome';
const SAIDA = path.resolve(__dirname, arg('saida', 'resultados/brutos.jsonl'));

const VIEWPORTS = { desktop: { width: 1440, height: 900, deviceScaleFactor: 1, isMobile: false, hasTouch: false, conteudo: 'd' },
                    movel: { width: 390, height: 844, deviceScaleFactor: 2, isMobile: true, hasTouch: true, conteudo: 'm' } };
const TECNICAS = [ { t: 'video', v: 'vp9-longo' }, { t: 'video', v: 'vp9-g30' }, { t: 'video', v: 'vp9-intra' }, { t: 'video', v: 'av1-intra' },
  { t: 'canvas-demanda' }, { t: 'canvas-todos' }, { t: 'canvas-janela', j: 8 }, { t: 'canvas-janela', j: 16 }, { t: 'canvas-janela', j: 32 }, { t: 'webcodecs', v: 'vp9-intra' } ];
const H264 = [ { t: 'video', v: 'h264-longo', uma: true }, { t: 'video', v: 'h264-g30', uma: true }, { t: 'video', v: 'h264-intra', uma: true } ];
const LERPS = [0, 0.15];
const CENARIOS = ['arremesso', 'constante', 'vai-e-volta', 'saltos'];

const PERFIS = {
  smoke: () => mk({ tecs: [TECNICAS[2], TECNICAS[5]], lerps: [0], cens: ['arremesso'], vps: ['desktop'], thr: [4], reps: 1 }),
  principal: () => [...mk({ tecs: [...TECNICAS, ...H264], lerps: LERPS, cens: CENARIOS, vps: ['desktop'], thr: [4], reps: 2 })],
  d1x: () => mk({ tecs: TECNICAS, lerps: LERPS, cens: ['arremesso', 'constante'], vps: ['desktop'], thr: [1], reps: 1 }),
  d6x: () => mk({ tecs: TECNICAS, lerps: LERPS, cens: ['arremesso', 'constante'], vps: ['desktop'], thr: [6], reps: 1 }),
  m4x: () => mk({ tecs: TECNICAS, lerps: LERPS, cens: ['arremesso', 'constante', 'vai-e-volta'], vps: ['movel'], thr: [4], reps: 1 }),
  carga: () => mk({ tecs: [...TECNICAS], lerps: [0], cens: ['carga'], vps: ['desktop', 'movel'], thr: [4], reps: 1 }),
  ablacao: () => [
    ...mk({ tecs: [{ t: 'canvas-janela', j: 16, cancelar: 0 }], lerps: [0], cens: ['arremesso', 'saltos'], vps: ['desktop'], thr: [4], reps: 2 }),
    ...mk({ tecs: [{ t: 'video', v: 'vp9-intra', fonte: 'blob' }], lerps: [0], cens: ['arremesso', 'constante'], vps: ['desktop'], thr: [4], reps: 2 }),
    ...mk({ tecs: [{ t: 'canvas-demanda', formato: 'avif' }, { t: 'canvas-todos', formato: 'avif' }, { t: 'canvas-janela', j: 16, formato: 'avif' }], lerps: [0], cens: ['arremesso'], vps: ['desktop'], thr: [4], reps: 2 }),
    ...mk({ tecs: [{ t: 'video', v: 'vp9-intra' }, { t: 'canvas-janela', j: 16 }, { t: 'canvas-todos' }], lerps: [0.08, 0.3], cens: ['arremesso', 'constante'], vps: ['desktop'], thr: [4], reps: 2 }),
    // 2 CPUs só (taskset): as threads de decodificação/rasterização brigam por CPU, como num aparelho fraco
    ...mk({ tecs: [{ t: 'video', v: 'vp9-intra' }, { t: 'video', v: 'vp9-longo' }, { t: 'canvas-janela', j: 16 }, { t: 'canvas-todos' }, { t: 'canvas-demanda' }], lerps: [0], cens: ['arremesso', 'constante'], vps: ['desktop'], thr: [4], reps: 1, cpus: 2 }),
  ],
};
function mk({ tecs, lerps, cens, vps, thr, reps, cpus }) { const out = [];
  for (let rep = 1; rep <= reps; rep++) for (const vp of vps) for (const th of thr) for (const cen of cens) for (const tc of tecs) for (const lerp of (tc.uma ? [0] : lerps)) {
    if (tc.uma && (cen !== cens[0] || rep > 1)) continue;
    out.push({ ...tc, lerp, cen, vp, th, rep, cpus: cpus || 4 }); }
  return out; }
const idDe = c => [c.vp, c.t, c.v || '', c.j || '', c.fonte || '', c.formato || '', c.cancelar === 0 ? 'semcancel' : '', 'lerp' + c.lerp, c.cen, c.th + 'x', c.cpus + 'cpu'].join('|');

// ---------- utilidades de sistema ----------
const procs = () => { const m = new Map(); for (const d of fs.readdirSync('/proc')) { if (!/^\d+$/.test(d)) continue; try { const st = fs.readFileSync(`/proc/${d}/stat`, 'utf8');
  const ppid = +st.slice(st.lastIndexOf(')') + 2).split(' ')[1]; m.set(+d, { ppid, cmd: fs.readFileSync(`/proc/${d}/cmdline`, 'utf8') }); } catch (e) {} } return m; };
const arvore = raiz => { const m = procs(), r = new Set([raiz]); let mudou = true; while (mudou) { mudou = false; for (const [p, i] of m) if (!r.has(p) && r.has(i.ppid)) { r.add(p); mudou = true; } } return [...r]; };
const achaRaiz = () => { for (const [p, i] of procs()) if (i.cmd.includes(EXE) && !i.cmd.includes('--type=')) return p; return null; };
const pssMB = pids => { let kb = 0; for (const p of pids) { try { const t = fs.readFileSync(`/proc/${p}/smaps_rollup`, 'utf8'); const m = /Pss:\s+(\d+)/.exec(t); if (m) kb += +m[1]; } catch (e) {} } return +(kb / 1024).toFixed(0); };
const agora = () => Number(process.hrtime.bigint()) / 1e6;
const ate = async t => { for (;;) { const d = t - agora(); if (d <= 0) return; await new Promise(r => d > 3 ? setTimeout(r, d - 2) : setImmediate(r)); } };
const dormir = ms => new Promise(r => setTimeout(r, ms));
const prng = a => () => { a |= 0; a = a + 0x6D2B79F5 | 0; let t = Math.imul(a ^ a >>> 15, 1 | a); t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t; return ((t ^ t >>> 14) >>> 0) / 4294967296; };

// ---------- cenários: entradas reais de roda (CDP Input.dispatchMouseEvent) ----------
async function cenario(nome, { cdp, page, geo }) {
  const L = geo.len, cx = Math.round(geo.W / 2), cy = Math.round(geo.H / 2), info = { rajadas: [] };
  const wheel = dy => cdp.send('Input.dispatchMouseEvent', { type: 'mouseWheel', x: cx, y: cy, deltaX: 0, deltaY: dy });
  const rajada = async (dyTotal, passo, durMs) => { const n = Math.max(1, Math.round(Math.abs(dyTotal) / passo)), ps = [], t0 = agora();
    for (let k = 0; k < n; k++) { await ate(t0 + k * durMs / n); ps.push(wheel(Math.sign(dyTotal) * passo)); } await Promise.all(ps); info.rajadas.push(Math.round(agora() - t0)); };
  if (nome === 'constante') { for (let leg = 0; leg < 3; leg++) await rajada(leg % 2 === 0 ? L : -L, 100, Math.round(L / 100) * 100); }
  else if (nome === 'arremesso') { await rajada(3000, 100, 200); await dormir(1500); await rajada(-3000, 100, 200); await dormir(1500); await rajada(3000, 100, 200); }
  else if (nome === 'vai-e-volta') { for (let c = 0; c < 4; c++) { await rajada(1200, 100, 600); await rajada(-1200, 100, 600); } }
  else if (nome === 'saltos') { const rnd = prng(42); let t0 = agora(); for (let k = 1; k <= 10; k++) { await ate(t0 + k * 700); const y = Math.round(geo.top + rnd() * L); await page.evaluate(y => scrollTo(0, y), y); } }
  else throw new Error('cenário desconhecido ' + nome);
  return info;
}
const inicioDe = { 'vai-e-volta': 0.25 };

// ---------- uma execução ----------
async function executar(srv, c) {
  const vp = VIEWPORTS[c.vp], t00 = agora();
  const exe = c.cpus < 4 ? path.join(__dirname, '_gerado', `chrome-${c.cpus}cpu.sh`) : EXE;
  if (c.cpus < 4) { fs.writeFileSync(exe, `#!/bin/sh\nexec taskset -c 0-${c.cpus - 1} ${EXE} "$@"\n`, { mode: 0o755 }); }
  const browser = await chromium.launch({ executablePath: exe, headless: true, args: ['--no-sandbox'] });
  const linha = { id: idDe(c), rep: c.rep, cfg: c, ambiente: { navegador: browser.version(), cpus: os.cpus().length, taskset: c.cpus } };
  try {
    const ctx = await browser.newContext({ viewport: { width: vp.width, height: vp.height }, deviceScaleFactor: vp.deviceScaleFactor, isMobile: vp.isMobile, hasTouch: vp.hasTouch });
    const page = await ctx.newPage(), cdp = await ctx.newCDPSession(page); await cdp.send('Performance.enable');
    const raiz = achaRaiz(); const pss = () => raiz ? pssMB(arvore(raiz)) : null;
    const q = new URLSearchParams({ tecnica: c.t, lerp: c.lerp, conteudo: vp.conteudo }); if (c.v) q.set('variante', c.v); if (c.j) q.set('janela', c.j);
    if (c.fonte) q.set('fonte', c.fonte); if (c.formato) q.set('formato', c.formato); if (c.cancelar === 0) q.set('cancelar', 0);
    const cargaSob = c.cen === 'carga' ? c.th : 1;                    // perfil "carga": throttle desde a navegação
    await cdp.send('Emulation.setCPUThrottlingRate', { rate: cargaSob });
    await fetch(srv.url + '/__reset'); const tNav = agora();
    await page.goto(`${srv.url}/pagina.html?${q}`, { waitUntil: 'load' });
    await page.waitForFunction(() => window.__bench && (window.__bench.pronto || window.__bench.erro), null, { timeout: 120000, polling: 100 });
    const b = await page.evaluate(() => ({ nao: __bench.naoSuportado, erro: __bench.erro, tPrimeiro: __bench.tPrimeiro, tPronto: __bench.tPronto, geo: __bench.geo, carregados: __bench.carregados }));
    await dormir(600);
    const bytes = await (await fetch(srv.url + '/__bytes')).json();
    linha.carga = { tPrimeiroMs: b.tPrimeiro && Math.round(b.tPrimeiro), tProntoMs: b.tPronto && Math.round(b.tPronto), bytes: bytes.bytes, pedidos: bytes.pedidos, pssMB: pss(), carregados: b.carregados, geo: b.geo };
    if (b.nao) { linha.naoSuportado = b.nao; return linha; }
    if (b.erro) { linha.erro = b.erro; return linha; }
    if (c.cen === 'carga') return linha;
    // posiciona o scroll no início do cenário e espera o quadro certo
    const p0 = inicioDe[c.cen] || 0, y0 = Math.round(b.geo.top + p0 * b.geo.len), q0 = Math.round(p0 * 149);
    await page.evaluate(y => scrollTo(0, y), y0);
    await page.waitForFunction(q => __bench.estado().drawn === q, q0, { timeout: 20000, polling: 50 }).catch(() => { linha.aviso = 'quadro inicial não chegou'; });
    await dormir(400);
    await cdp.send('Emulation.setCPUThrottlingRate', { rate: c.th });
    const m = a => Object.fromEntries(a.metrics.map(x => [x.name, x.value])), m0 = m(await cdp.send('Performance.getMetrics'));
    let pico = pss(); const timer = setInterval(() => { const v = pss(); if (v > pico) pico = v; }, 500);
    await page.evaluate(() => __bench.iniciar());
    const tIni = agora(); linha.entrada = await cenario(c.cen, { cdp, page, geo: b.geo }); await dormir(1800);
    const r = await page.evaluate(() => __bench.parar()); const durNode = agora() - tIni; clearInterval(timer);
    const m1 = m(await cdp.send('Performance.getMetrics')), bytes2 = await (await fetch(srv.url + '/__bytes')).json();
    linha.r = r; linha.durNodeMs = Math.round(durNode);
    linha.cpuPagina = { scriptMs: +((m1.ScriptDuration - m0.ScriptDuration) * 1000).toFixed(0), tarefaMs: +((m1.TaskDuration - m0.TaskDuration) * 1000).toFixed(0), layoutMs: +((m1.LayoutDuration - m0.LayoutDuration) * 1000).toFixed(0), jsHeapMB: +(m1.JSHeapUsedSize / 1048576).toFixed(1) };
    linha.mem = { pssPicoMB: pico, pssFimMB: pss() }; linha.bytesScroll = bytes2.bytes - bytes.bytes; linha.bytesTotal = bytes2.bytes;
  } catch (e) { linha.erro = String(e && e.message || e).split('\n')[0]; }
  finally { await browser.close().catch(() => {}); linha.tempoTotalS = +((agora() - t00) / 1000).toFixed(1); }
  return linha;
}

(async () => {
  const perfis = arg('perfil', 'smoke').split(','), so = arg('so') && new RegExp(arg('so')), reps = arg('reps') && +arg('reps');
  let lista = perfis.flatMap(p => (PERFIS[p] || (() => { throw new Error('perfil desconhecido ' + p); }))().map(c => ({ ...c, perfil: p })));
  if (reps) lista = lista.filter(c => c.rep <= reps);
  if (so) lista = lista.filter(c => so.test(idDe(c)));
  const feitos = new Set(); if (fs.existsSync(SAIDA)) for (const l of fs.readFileSync(SAIDA, 'utf8').split('\n')) { if (!l) continue; try { const j = JSON.parse(l); if (!j.erro || /naoSuportado/.test(l)) feitos.add(j.id + '#' + j.rep); } catch (e) {} }
  const pend = lista.filter(c => !feitos.has(idDe(c) + '#' + c.rep));
  console.log(`${lista.length} execuções no plano, ${pend.length} pendentes, saída ${SAIDA}`);
  const srv = await iniciar(0, __dirname); fs.mkdirSync(path.dirname(SAIDA), { recursive: true });
  let k = 0; const t0 = Date.now();
  for (const c of pend) { k++; const l = await executar(srv, c); l.perfil = c.perfil; fs.appendFileSync(SAIDA, JSON.stringify(l) + '\n');
    const r = l.r; console.log(`[${k}/${pend.length}] ${l.id} #${c.rep} ${l.naoSuportado ? 'NÃO SUPORTADO' : l.erro ? 'ERRO ' + l.erro : r ? `p95=${r.intervalo.p95?.toFixed(1)}ms >50=${r.intervalo.pct50?.toFixed(1)}% atraso p95=${r.atraso.p95} max=${r.atraso.max} lt=${r.longtasks.n}` : 'carga ' + l.carga.tPrimeiroMs + '/' + l.carga.tProntoMs + 'ms'} (${l.tempoTotalS}s; decorrido ${Math.round((Date.now() - t0) / 60000)} min)`); }
  srv.srv.close(); process.exit(0);
})().catch(e => { console.error(e); process.exit(1); });
