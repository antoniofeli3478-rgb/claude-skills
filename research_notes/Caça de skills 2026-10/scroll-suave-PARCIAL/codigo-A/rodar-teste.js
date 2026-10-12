const path = require('path');
const req = m => { try { return require(m); } catch { return require(path.join('/opt/node-tools/node_modules', m)); } };
const { chromium } = req('playwright');
const base = process.argv[2];
(async () => {
  let browser;
  try { browser = await chromium.launch(); } catch { browser = await chromium.launch({ executablePath: '/opt/pw-browsers/chromium-1194/chrome-linux/chrome' }); }
  const res = {};
  for (const [nome, rate, medirFlag] of [['sem-medidor-idle', 1, false], ['cpu1x', 1, true], ['cpu4x', 4, true], ['cpu6x', 6, true]]) {
    const ctx = await browser.newContext({ viewport: { width: 1280, height: 720 }, deviceScaleFactor: 1 });
    const page = await ctx.newPage();
    const cdp = await ctx.newCDPSession(page);
    await cdp.send('Emulation.setCPUThrottlingRate', { rate });
    await page.goto(`${base}/teste.html${medirFlag ? '?medir=1' : ''}`);
    await page.waitForFunction(() => window.__t && window.__t.stats().prontos >= 8, null, { timeout: 20000 });
    await page.mouse.move(640, 360);
    const rolar = async (dy, passos, intervalo) => { for (let i = 0; i < passos; i++) { await page.mouse.wheel(0, dy); await page.waitForTimeout(intervalo); } };
    // desce com passos discretos de roda (80 px a cada 16 ms ≈ 5000 px/s), sobe, desce de novo mais devagar
    await rolar(80, 27, 16); await rolar(-80, 27, 16); await rolar(40, 54, 16);
    await page.waitForTimeout(800);
    const antes = await page.evaluate(() => window.__t.stats().ticks);
    await page.waitForTimeout(1000);
    const depois = await page.evaluate(() => window.__t.stats());
    res[nome] = { stats: depois, ticksOciosos_em_1s: depois.ticks - antes };
    if (medirFlag) res[nome].medida = await page.evaluate(() => { const r = window.__t.medir(); r.loaf = r.loaf.length; return r; });
    await ctx.close();
  }
  console.log(JSON.stringify(res, null, 1));
  await browser.close();
})().catch(e => { console.error(e); process.exit(1); });
