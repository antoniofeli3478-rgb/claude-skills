const path = require('path');
const req = m => { try { return require(m); } catch { return require(path.join('/opt/node-tools/node_modules', m)); } };
const { chromium } = req('playwright');
(async () => {
  let b; try { b = await chromium.launch(); } catch { b = await chromium.launch({ executablePath: '/opt/pw-browsers/chromium-1194/chrome-linux/chrome' }); }
  const ctx = await b.newContext({ viewport: { width: 1280, height: 720 }, deviceScaleFactor: 1 });
  const page = await ctx.newPage(); const cdp = await ctx.newCDPSession(page);
  await cdp.send('Network.enable');
  await cdp.send('Network.setCacheDisabled', { cacheDisabled: true });
  await cdp.send('Network.emulateNetworkConditions', { offline: false, latency: 120, downloadThroughput: 400 * 1024, uploadThroughput: 400 * 1024 }); // ~3,2 Mbps, 120 ms
  await page.goto(process.argv[2] + '/teste.html');
  await page.waitForFunction(() => window.__t && window.__t.stats().prontos >= 3, null, { timeout: 30000 });
  const saltos = [0.9, 0.1, 0.6, 0.2, 0.8, 0.05];
  const geo = await page.evaluate(() => ({ len: document.getElementById('cine').offsetHeight - innerHeight }));
  const amostras = [];
  for (const p of saltos) { await page.evaluate(y => window.scrollTo(0, y), Math.round(p * geo.len)); await page.waitForTimeout(450);
    amostras.push(await page.evaluate(() => { const s = window.__t.stats(); return { pedido: s.pedido, prontos: s.prontos, voando: s.voando }; })); }
  await page.waitForTimeout(4000);
  const fim = await page.evaluate(() => window.__t.stats());
  console.log(JSON.stringify({ saltos, amostras, fim }, null, 1));
  await b.close();
})().catch(e => { console.error(e); process.exit(1); });
