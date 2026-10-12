#!/usr/bin/env bash
# Gera o CONTEÚDO DE TESTE e as variantes de codificação do benchmark (tudo em _gerado/, fora do repositório).
# Uso: bash gerar-conteudo.sh [etapa]     etapa = fonte | variantes | tudo (padrão)
# Conteúdo difícil de comprimir: fractal de Mandelbrot em zoom + ruído temporal (grão) + número do quadro desenhado.
# 150 quadros, 30 fps. d = 1280x720 (desktop), m = 720x1280 (celular).
set -euo pipefail
cd "$(dirname "$0")"; G=_gerado; mkdir -p $G
FONT=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf
F=(-hide_banner -loglevel error -y)
N=150; FPS=30
ETAPA=${1:-tudo}

fonte() { # $1 = d|m, $2 = LxA do quadro final
  local k=$1 s=$2
  [[ -f $G/fonte-$k.mkv ]] && return
  # 1) imagem fixa grande de Mandelbrot (vale do cavalo-marinho, cor por convergência: sem áreas pretas vazias)
  local big=$([[ $k == d ]] && echo 2560x1440 || echo 1440x2560)
  [[ -f $G/still-$k.png ]] || ffmpeg "${F[@]}" -f lavfi -i "mandelbrot=s=$big:r=1:maxiter=1000:start_x=-0.7436:start_y=0.1318:start_scale=0.003:end_scale=0.003:end_pts=5:inner=convergence:outer=normalized_iteration_count" -frames:v 1 $G/still-$k.png
  # 2) câmera: zoom de 1x a 1,6x com pan diagonal (movimento coerente) + grão temporal leve + número do quadro
  ffmpeg "${F[@]}" -loop 1 -framerate $FPS -i $G/still-$k.png \
    -vf "zoompan=z='1+0.6*on/149':x='(iw-iw/zoom)*on/149':y='(ih-ih/zoom)*(0.2+0.6*on/149)':d=1:s=$s:fps=$FPS,noise=alls=5:allf=t+u,drawtext=fontfile=$FONT:text='%{eif\:n+1\:d\:3}':fontsize=200:fontcolor=white:box=1:boxcolor=black@0.55:boxborderw=16:x=(w-tw)/2:y=(h-th)/2,format=yuv444p" \
    -frames:v $N -c:v ffv1 $G/fonte-$k.mkv
}

variantes() { # $1 = d|m
  local k=$1 S=$G/fonte-$1.mkv
  local V=(-an -pix_fmt yuv420p -r $FPS)
  # v1/v2/v3: H.264 (o Chromium do Playwright NÃO toca H.264: aqui só vale o TAMANHO)
  [[ -f $G/$k-h264-longo.mp4 ]] || ffmpeg "${F[@]}" -i $S "${V[@]}" -c:v libx264 -crf 23 -movflags +faststart $G/$k-h264-longo.mp4
  [[ -f $G/$k-h264-g30.mp4 ]]   || ffmpeg "${F[@]}" -i $S "${V[@]}" -c:v libx264 -crf 23 -g 30 -movflags +faststart $G/$k-h264-g30.mp4
  [[ -f $G/$k-h264-intra.mp4 ]] || ffmpeg "${F[@]}" -i $S "${V[@]}" -c:v libx264 -crf 23 -g 1 -bf 0 -movflags +faststart $G/$k-h264-intra.mp4
  [[ -f $G/$k-h264-intra-crf20.mp4 ]] || ffmpeg "${F[@]}" -i $S "${V[@]}" -c:v libx264 -crf 20 -g 1 -bf 0 -movflags +faststart $G/$k-h264-intra-crf20.mp4
  # v4: VP9 (toca no Chromium): GOP padrão, GOP 30 e all-intra
  local VP=(-c:v libvpx-vp9 -crf 32 -b:v 0 -row-mt 1 -threads 4 -deadline good -cpu-used 4)
  [[ -f $G/$k-vp9-longo.webm ]] || ffmpeg "${F[@]}" -i $S "${V[@]}" "${VP[@]}" $G/$k-vp9-longo.webm
  [[ -f $G/$k-vp9-g30.webm ]]   || ffmpeg "${F[@]}" -i $S "${V[@]}" "${VP[@]}" -g 30 $G/$k-vp9-g30.webm
  [[ -f $G/$k-vp9-intra.webm ]] || ffmpeg "${F[@]}" -i $S "${V[@]}" "${VP[@]}" -g 1 $G/$k-vp9-intra.webm
  # quadros VP9 all-intra crus em IVF (para o WebCodecs: contêiner trivial, sem demuxer de terceiros)
  [[ -f $G/$k-vp9-intra.ivf ]] || ffmpeg "${F[@]}" -i $G/$k-vp9-intra.webm -c:v copy -f ivf $G/$k-vp9-intra.ivf
  # AV1 all-intra (toca no Chromium por software). libaom com -g 1 estoura uma asserção no ffmpeg 6.1; SVT-AV1 funciona.
  [[ -f $G/$k-av1-intra.mp4 ]] || ffmpeg "${F[@]}" -i $S "${V[@]}" -c:v libsvtav1 -crf 38 -preset 8 -g 1 -movflags +faststart $G/$k-av1-intra.mp4
  # v5: sequências de imagens. WebP q75 (compression_level 4); AVIF (libaom, crf 32) um arquivo por quadro.
  mkdir -p $G/$k-webp $G/$k-avif
  [[ -f $G/$k-webp/f_0150.webp ]] || ffmpeg "${F[@]}" -i $S -an -vf format=yuv420p -c:v libwebp -quality 75 -compression_level 4 $G/$k-webp/f_%04d.webp
  if [[ ! -f $G/$k-avif/f_0150.avif ]]; then
    seq 1 $N | xargs -P4 -I{} bash -c 'i={}; t=$(awk -v i=$i "BEGIN{printf \"%.4f\", (i-1)/30}"); ffmpeg -hide_banner -loglevel error -y -ss $t -i '"$S"' -frames:v 1 -vf format=yuv420p -c:v libaom-av1 -crf 32 -b:v 0 -cpu-used 8 -still-picture 1 '"$G/$k"'-avif/f_$(printf %04d $i).avif'
  fi
}

[[ $ETAPA == fonte || $ETAPA == tudo ]] && { fonte d 1280x720 & fonte m 720x1280 & wait; }
[[ $ETAPA == variantes || $ETAPA == tudo ]] && { variantes d; variantes m; }
echo "ok"
