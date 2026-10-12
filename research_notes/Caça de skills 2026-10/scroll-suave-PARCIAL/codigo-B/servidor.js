// Servidor estático mínimo (sem dependências) para o benchmark: suporta Range (vídeo), conta bytes enviados
// e serve com cache (como um site de verdade). Uso: node servidor.js [porta=8123]   ou   require('./servidor').iniciar()
const http = require('http'), fs = require('fs'), path = require('path');
const TIPOS = { '.html': 'text/html; charset=utf-8', '.js': 'text/javascript', '.json': 'application/json', '.webp': 'image/webp',
  '.avif': 'image/avif', '.mp4': 'video/mp4', '.webm': 'video/webm', '.png': 'image/png', '.ivf': 'application/octet-stream' };

function iniciar(porta = 0, raiz = __dirname) {
  const cont = { bytes: 0, pedidos: 0 };
  const srv = http.createServer((req, res) => {
    const u = new URL(req.url, 'http://x');
    if (u.pathname === '/__reset') { cont.bytes = 0; cont.pedidos = 0; return res.end('ok'); }
    if (u.pathname === '/__bytes') { res.setHeader('content-type', 'application/json'); return res.end(JSON.stringify(cont)); }
    const arq = path.join(raiz, decodeURIComponent(u.pathname));
    if (!arq.startsWith(raiz) || !fs.existsSync(arq) || !fs.statSync(arq).isFile()) { res.statusCode = 404; return res.end('nao achei'); }
    const tam = fs.statSync(arq).size, h = { 'content-type': TIPOS[path.extname(arq)] || 'application/octet-stream',
      'accept-ranges': 'bytes', 'cache-control': u.pathname.startsWith('/_gerado/') ? 'public, max-age=3600' : 'no-cache' };
    let ini = 0, fim = tam - 1, status = 200;
    const m = /bytes=(\d*)-(\d*)/.exec(req.headers.range || '');
    if (m) { status = 206; if (m[1] === '') { ini = Math.max(0, tam - +m[2]); } else { ini = +m[1]; if (m[2] !== '') fim = Math.min(+m[2], tam - 1); }
      h['content-range'] = `bytes ${ini}-${fim}/${tam}`; }
    h['content-length'] = fim - ini + 1; res.writeHead(status, h); cont.pedidos++;
    const s = fs.createReadStream(arq, { start: ini, end: fim });
    s.on('data', d => { cont.bytes += d.length; }); s.on('error', () => res.destroy()); res.on('close', () => s.destroy()); s.pipe(res);
  });
  return new Promise(ok => srv.listen(porta, '127.0.0.1', () => ok({ srv, cont, porta: srv.address().port, url: `http://127.0.0.1:${srv.address().port}` })));
}
module.exports = { iniciar };
if (require.main === module) iniciar(+process.argv[2] || 8123).then(s => console.log('servindo', s.url));
