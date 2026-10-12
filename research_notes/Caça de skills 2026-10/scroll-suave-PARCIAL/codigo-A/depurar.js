const path = require('path');
const req = m => { try { return require(m); } catch { return require(path.join('/opt/node-tools/node_modules', m)); } };
const { chromium } = req('playwright');
(async () => {
  let b; try { b = await chromium.launch(); } catch { b = await chromium.launch({ executablePath: '/opt/pw-browsers/chromium-1194/chrome-linux/chrome' }); }
  const p = await b.newPage({ viewport: { width: 1280, height: 720 } });
  p.on('console', m => console.log('console:', m.text())); p.on('pageerror', e => console.log('pageerror:', e.message));
  p.on('requestfailed', r => console.log('falhou:', r.url()));
  await p.goto(process.argv[2] + '/teste.html'); await p.waitForTimeout(2500);
  console.log(await p.evaluate(() => JSON.stringify(window.__t && window.__t.stats())));
  await b.close();
})();
