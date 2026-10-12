#!/usr/bin/env bash
# Verifica as receitas ffmpeg da pesquisa A com uma fonte sintética (mandelbrot, 90 quadros 1280x720 30 fps).
set -u
O=${1:?pasta de saída}; mkdir -p "$O"; cd "$O"
F=(-hide_banner -loglevel error -y)
W=1280; N=90
# fonte opaca (conteúdo complexo) e fonte com alpha (círculo)
ffmpeg "${F[@]}" -f lavfi -i "mandelbrot=size=${W}x720:rate=30" -frames:v $N -c:v ffv1 src.mkv
ffmpeg "${F[@]}" -i src.mkv -vf "format=yuva444p,geq=lum='lum(X,Y)':cb='cb(X,Y)':cr='cr(X,Y)':a='if(lt(hypot(X-640,Y-360),300),255,0)'" -c:v ffv1 src_alpha.mkv
t(){ local n=$1; shift; local s=$(date +%s.%N); "$@" 2>>erros.txt; local rc=$?; local e=$(date +%s.%N); printf '%s rc=%s enc=%.1fs\n' "$n" "$rc" "$(echo "$e - $s" | bc)" >> tempos.txt; }
: > erros.txt; : > tempos.txt
# 1 H.264: GOP padrão (referência)
t h264_gop250 ffmpeg "${F[@]}" -i src.mkv -an -c:v libx264 -crf 23 -g 250 -pix_fmt yuv420p -movflags +faststart h264_gop250.mp4
# 2 H.264 all-intra
t h264_ai ffmpeg "${F[@]}" -i src.mkv -an -c:v libx264 -crf 23 -g 1 -pix_fmt yuv420p -movflags +faststart h264_ai.mp4
# 3 H.264 GOP curto sem B-frames (decodificação em ordem), keyframe fixo a cada 8
t h264_g8 ffmpeg "${F[@]}" -i src.mkv -an -c:v libx264 -crf 23 -g 8 -keyint_min 8 -sc_threshold 0 -bf 0 -pix_fmt yuv420p -movflags +faststart h264_g8.mp4
t h264_g15 ffmpeg "${F[@]}" -i src.mkv -an -c:v libx264 -crf 23 -g 15 -keyint_min 15 -sc_threshold 0 -bf 0 -pix_fmt yuv420p -movflags +faststart h264_g15.mp4
# 4 VP9 all-intra e GOP curto
t vp9_ai ffmpeg "${F[@]}" -i src.mkv -an -c:v libvpx-vp9 -crf 32 -b:v 0 -g 1 -row-mt 1 -deadline good -cpu-used 4 -pix_fmt yuv420p vp9_ai.webm
t vp9_g8 ffmpeg "${F[@]}" -i src.mkv -an -c:v libvpx-vp9 -crf 32 -b:v 0 -g 8 -keyint_min 8 -lag-in-frames 0 -auto-alt-ref 0 -row-mt 1 -deadline good -cpu-used 4 -pix_fmt yuv420p vp9_g8.webm
# 5 AV1 all-intra (SVT-AV1 e libaom)
t av1_svt_ai ffmpeg "${F[@]}" -i src.mkv -an -c:v libsvtav1 -crf 35 -preset 8 -g 1 -pix_fmt yuv420p av1_svt_ai.mp4
t av1_aom_ai ffmpeg "${F[@]}" -i src.mkv -an -c:v libaom-av1 -crf 35 -b:v 0 -g 1 -cpu-used 8 -row-mt 1 -pix_fmt yuv420p av1_aom_ai.mp4
# 6 HEVC (x265) all-intra e GOP curto (sem alpha; alpha só com VideoToolbox no macOS)
t hevc_ai ffmpeg "${F[@]}" -i src.mkv -an -c:v libx265 -crf 26 -preset fast -x265-params keyint=1:no-scenecut=1:log-level=error -tag:v hvc1 -pix_fmt yuv420p -movflags +faststart hevc_ai.mp4
# 7 VP9 com alpha (Chrome/Firefox) – all-intra
t vp9_alpha ffmpeg "${F[@]}" -i src_alpha.mkv -an -c:v libvpx-vp9 -pix_fmt yuva420p -crf 32 -b:v 0 -g 1 -auto-alt-ref 0 -row-mt 1 -deadline good -cpu-used 4 vp9_alpha.webm
# 8 alpha empilhado (stacked alpha): cor em cima, alfa (cinza) embaixo; H.264 all-intra, funciona em qualquer navegador
t stacked_h264_ai ffmpeg "${F[@]}" -i src_alpha.mkv -filter_complex "[0:v]format=yuva444p,split[a][b];[b]alphaextract[al];[a]format=yuv420p[c];[al]format=yuv420p[al2];[c][al2]vstack" -an -c:v libx264 -crf 23 -g 1 -pix_fmt yuv420p -movflags +faststart stacked_h264_ai.mp4
# 9 sequências de imagens a partir do vídeo
mkdir -p webp jpg avif webp_alpha
t webp ffmpeg "${F[@]}" -i src.mkv -an -c:v libwebp -quality 78 -compression_level 6 webp/f_%04d.webp
t jpg ffmpeg "${F[@]}" -i src.mkv -an -q:v 4 jpg/f_%04d.jpg
t webp_alpha ffmpeg "${F[@]}" -i src_alpha.mkv -an -c:v libwebp -quality 78 -compression_level 6 webp_alpha/f_%04d.webp
t avif ffmpeg "${F[@]}" -i src.mkv -an -frames:v 30 -c:v libaom-av1 -crf 32 -b:v 0 -cpu-used 8 -still-picture 1 avif/f_%04d.avif
echo done
