import subprocess, sys, os, json, struct
def keys(f):
    out = subprocess.run(['ffprobe','-v','error','-select_streams','v:0','-show_entries','packet=flags','-of','csv=p=0',f],capture_output=True,text=True).stdout.split()
    return [('K' in x) for x in out]
def atoms(f):
    r=[]; 
    with open(f,'rb') as fh:
        while True:
            h=fh.read(8)
            if len(h)<8: break
            sz,t=struct.unpack('>I4s',h); t=t.decode('latin1')
            if sz==1: sz=struct.unpack('>Q',fh.read(8))[0]; fh.seek(sz-16,1)
            else: fh.seek(sz-8,1)
            r.append(t)
    return r
for f in ['h264_gop250.mp4','h264_g15.mp4','h264_g8.mp4','h264_ai.mp4','vp9_ai.webm','vp9_g8.webm','av1_svt_ai.mp4','av1_aom_ai.mp4','hevc_ai.mp4','vp9_alpha.webm','stacked_h264_ai.mp4']:
    if not os.path.exists(f): continue
    k = keys(f)
    n=len(k)
    if n==0: print(f,'sem pacotes (arquivo corrompido?)', os.path.getsize(f)); continue
    # custo de seek (em quadros decodificados): para cada quadro i, quantos quadros desde o ultimo keyframe
    last=0; cost=[]
    for i,x in enumerate(k):
        if x: last=i
        cost.append(i-last+1)
    pf = subprocess.run(['ffprobe','-v','error','-select_streams','v:0','-show_entries','stream=width,height,pix_fmt,codec_name','-of','csv=p=0',f],capture_output=True,text=True).stdout.strip()
    at = atoms(f)[:3] if f.endswith('.mp4') else ''
    print(f"{f:20s} {os.path.getsize(f)/1e6:6.2f} MB  {os.path.getsize(f)/n/1e3:7.1f} KB/q  quadros={n} keyframes={sum(k)} seek_medio={sum(cost)/n:5.1f} seek_max={max(cost):3d}  {pf}  {at}")
