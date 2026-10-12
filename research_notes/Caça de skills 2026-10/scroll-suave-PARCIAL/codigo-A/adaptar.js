// Escolhe 'poster' | 'lite' | 'normal' SEM sniffing de user agent; tudo com fallback (Safari não tem saveData/deviceMemory/effectiveType).
export function escolherPerfil() {
  const c = navigator.connection || {};
  if (matchMedia('(prefers-reduced-motion: reduce)').matches) return 'poster';
  if (c.saveData) return 'poster';
  if (['slow-2g', '2g'].includes(c.effectiveType)) return 'poster';
  let pontos = 0;
  if (c.effectiveType === '3g') pontos += 2;
  if (navigator.deviceMemory && navigator.deviceMemory <= 2) pontos += 2;     // só Chrome/Edge; valor arredondado
  if (navigator.hardwareConcurrency && navigator.hardwareConcurrency <= 2) pontos += 1;
  if (matchMedia('(max-width: 700px)').matches && devicePixelRatio >= 3) pontos += 1; // celular com tela densa: DPR já é limitado a 2
  return pontos >= 2 ? 'lite' : 'normal';
}
export const dprMax = () => Math.min(devicePixelRatio || 1, 2);
// Rede de segurança em tempo de execução: se o p95 dos intervalos de quadro passar de 25 ms durante a rolagem, rebaixa.
export function vigiar(aoRebaixar, { janela = 90, limite = 25 } = {}) {
  const dts = []; let ant = 0, ativo = false, t = 0;
  addEventListener('scroll', () => { ativo = true; clearTimeout(t); t = setTimeout(() => { ativo = false; ant = 0; }, 150); }, { passive: true });
  const f = (agora) => { if (ativo && ant) { dts.push(agora - ant); if (dts.length >= janela) {
      const o = dts.splice(0).sort((a, b) => a - b); if (o[Math.floor(o.length * 0.95)] > limite) { aoRebaixar(); return; } } }
    ant = agora; requestAnimationFrame(f); };
  requestAnimationFrame(f);
}
