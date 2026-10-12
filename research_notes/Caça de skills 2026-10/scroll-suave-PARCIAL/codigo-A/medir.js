// Mede a suavidade DURANTE a rolagem. Chrome/Edge: Long Animation Frames. Todos: intervalos de rAF.
export function medir() {
  const dts = []; const loaf = []; let ant = 0, ativo = false, t = 0;
  try { new PerformanceObserver((l) => { for (const e of l.getEntries()) if (ativo) loaf.push({ d: e.duration, b: e.blockingDuration,
      scripts: e.scripts.map((s) => `${s.sourceFunctionName || s.invoker}:${Math.round(s.duration)}`) }); })
      .observe({ type: 'long-animation-frame', buffered: false }); } catch (_) { /* Safari/Firefox: sem LoAF */ }
  addEventListener('scroll', () => { ativo = true; clearTimeout(t); t = setTimeout(() => { ativo = false; ant = 0; }, 150); }, { passive: true });
  const f = (agora) => { if (ativo && ant) dts.push(agora - ant); ant = agora; requestAnimationFrame(f); };
  requestAnimationFrame(f);
  const pct = (a, p) => a.length ? a[Math.min(a.length - 1, Math.floor(a.length * p))] : 0;
  return () => { const o = [...dts].sort((a, b) => a - b);
    return { quadros: o.length, p50: pct(o, .5), p95: pct(o, .95), p99: pct(o, .99), max: o[o.length - 1] || 0,
      abaixo20: o.length ? o.filter((x) => x < 20).length / o.length : 0, acima50: o.filter((x) => x > 50).length, loaf }; };
}
