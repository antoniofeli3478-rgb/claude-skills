// Janela deslizante de quadros: só [i - atrás, i + frente] fica na memória.
// Código próprio (domínio público do projeto). fetch + createImageBitmap: a decodificação sai da thread principal.
export function criarJanela({ total, url, frente = 24, atras = 8, paralelo = 4, resize, aoChegar }) {
  const bmp = new Map();   // índice -> ImageBitmap pronto
  const voo = new Map();   // índice -> AbortController (em andamento)
  let centro = -1, dir = 1, vivo = true, falhas = 0;   // -1: a 1ª chamada de ir() sempre dispara a carga
  const dentro = (i) => i >= centro - (dir > 0 ? atras : frente) && i <= centro + (dir > 0 ? frente : atras);

  async function carregar(i) {
    const ac = new AbortController(); voo.set(i, ac);
    try {
      const r = await fetch(url(i), { signal: ac.signal });
      if (!r.ok) throw new Error(r.status);
      const b = await createImageBitmap(await r.blob(), resize);
      if (!vivo || !dentro(i)) { b.close(); return; }   // chegou tarde: descarta
      bmp.set(i, b); aoChegar && aoChegar(i);
    } catch (e) { if (e.name !== 'AbortError') falhas++; }
    finally { voo.delete(i); bombear(); }
  }

  function bombear() {
    if (!vivo) return;
    for (const [i, ac] of voo) if (!dentro(i)) ac.abort();                          // cancela o que saiu da janela
    for (const [i, b] of bmp) if (!dentro(i)) { b.close(); bmp.delete(i); }          // solta a memória
    for (let d = 0; d <= Math.max(frente, atras) && voo.size < paralelo; d++) {      // do mais perto ao mais longe
      for (const i of [centro + dir * d, centro - dir * d]) {
        if (voo.size >= paralelo) break;
        if (i >= 0 && i < total && dentro(i) && !bmp.has(i) && !voo.has(i)) carregar(i);
      }
    }
  }
  return {
    ir(i, d) { if (i === centro && (!d || d === dir)) return; if (d) dir = d; centro = i; bombear(); },
    // o quadro pronto mais próximo de i (nunca bloqueia; nunca devolve nulo se já houver algum quadro)
    perto(i) { for (let d = 0; d < total; d++) { if (bmp.has(i - d)) return bmp.get(i - d); if (bmp.has(i + d)) return bmp.get(i + d); } return null; },
    stats: () => ({ prontos: bmp.size, voando: voo.size, falhas }),
    fechar() { vivo = false; voo.forEach((a) => a.abort()); bmp.forEach((b) => b.close()); bmp.clear(); },
  };
}
