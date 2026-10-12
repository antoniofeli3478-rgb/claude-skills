// Inércia por tempo + rAF "sob demanda": o laço só roda enquanto |alvo - atual| > epsilon e a seção está visível.
export function criarScrub({ secao, total, desenhar, reduzir = false, k = 0.18, eps = 0.02 }) {
  let topo = 0, comp = 1, alvo = 0, atual = 0, raf = 0, ultimo = 0, visivel = true, ultimoIdx = -1;
  const lim = (x) => Math.min(1, Math.max(0, x));
  const medir = () => {                                   // geometria só aqui (ResizeObserver), nunca no scroll
    topo = secao.getBoundingClientRect().top + scrollY;
    comp = Math.max(1, secao.offsetHeight - innerHeight);
    acordar();
  };
  const tick = (agora) => {
    raf = 0;
    const dt = ultimo ? Math.min(64, agora - ultimo) : 16.7; ultimo = agora;
    alvo = lim((scrollY - topo) / comp);                  // única leitura por quadro: scrollY
    atual += (alvo - atual) * (reduzir ? 1 : 1 - Math.pow(1 - k, dt / 16.7));
    if (Math.abs(alvo - atual) * (total - 1) < eps) atual = alvo;   // epsilon em QUADROS, não em progresso
    const idx = Math.round(atual * (total - 1));
    if (idx !== ultimoIdx) { ultimoIdx = idx; desenhar(idx, atual); }  // só redesenha se o quadro mudou
    if (atual !== alvo && visivel && !document.hidden) acordar(); else ultimo = 0;   // dorme quando chega
  };
  const acordar = () => { if (!raf && visivel && !document.hidden) raf = requestAnimationFrame(tick); };
  addEventListener('scroll', acordar, { passive: true });
  document.addEventListener('visibilitychange', acordar);
  new IntersectionObserver(([e]) => { visivel = e.isIntersecting; acordar(); }, { rootMargin: '25% 0px' }).observe(secao);
  new ResizeObserver(medir).observe(document.documentElement);
  return { medir, get progresso() { return atual; } };
}
