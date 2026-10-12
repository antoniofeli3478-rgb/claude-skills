# Gera INDICE.md e as listas _lista.tsv do catálogo a partir das fichas.
# Uso: python3 gerar-indice.py <pasta do catálogo>   (precisa de PyYAML)
# Também confere os campos obrigatórios e as seções de cada ficha.
import sys,os,glob,re,yaml,collections
root=sys.argv[1]
areas={'design-movimento':'Movimento e botões (animação de interface)','design-ui':'UI/UX, estilos premium e a lista do Daniel','blender':'Blender e vídeo com IA','sites':'Sites com IA'}
req=['nome','status','categoria_nossa']
secs=['## O que é','## O que aproveitamos','## Transcrição']
rows={};problems=[]
for a in areas:
    rows[a]=[]
    for f in sorted(glob.glob(f'{root}/{a}/*.md')):
        b=os.path.basename(f)
        if b in('LEIA-ME.md',) or b.startswith('ferramentas-') or b.startswith('colecoes-'): continue
        s=open(f,encoding='utf-8').read()
        m=re.match(r'^---\n(.*?)\n---\n',s,flags=re.S)
        if not m: problems.append((a,b,'sem front matter (ficha livre)')); rows[a].append((b,{'nome':b[:-3],'status':'—','nota':'—','onde_entrou':'—'}));continue
        try: d=yaml.safe_load(m.group(1))
        except Exception as e: problems.append((a,b,'YAML inválido')); continue
        for k in req:
            if k not in d: problems.append((a,b,'falta '+k))
        for sec in secs:
            if sec not in s: problems.append((a,b,'falta seção '+sec))
        rows[a].append((b,d))
st=collections.Counter(); 
for a in rows:
    for b,d in rows[a]: st[str(d.get('status'))]+=1
out=['# Catálogo de skills pesquisadas (caça de skills, outubro de 2026)','',
'Uma ficha `.md` para cada skill, MCP ou fonte que avaliamos. Cada ficha tem: origem, instalações, status, onde entrou na nossa estrutura, o que aproveitamos, o que não aproveitamos, riscos e a **transcrição** (o que aprendemos, nas nossas palavras).','',
'**Como ler o status:**','- `instalada-intacta`: está no repositório em `skills/`, cópia sem alteração (o `ORIGEM.md` de cada skill diz de onde veio).','- `proposta-aguardando-aprovacao`: foi proposta e ainda espera a decisão do Daniel.','- `ideias-aproveitadas`: não copiamos, mas as ideias entraram numa skill nossa.','- `so-referencia`: avaliada e guardada só como referência.','- `descartada`: não serve, com o motivo na ficha.','',
'Os status foram atualizados em 12/10/2026, depois da aprovação do Daniel (PR #21). Os caminhos de `fontes_locais` que começam com `scratchpad/` apontavam para uma pasta temporária da sessão; o conteúdo equivalente está em `research_notes/Caça de skills 2026-10/`.','','Os arquivos `TRANSCRICAO.md` completos de cada tema ficam em `research_notes/Caça de skills 2026-10/<tema>/`. O índice das skills instaladas é o `guia/HABILIDADES.txt`.','',
f'**Total: {sum(len(v) for v in rows.values())} fichas.** Por status: '+', '.join(f'{k} {v}' for k,v in sorted(st.items(),key=lambda x:-x[1]))+'.','']
for a,t in areas.items():
    out+= [f'## {t} ({len(rows[a])} fichas)','','| Skill | Status | Nota | Entrou em |','|---|---|---|---|']
    for b,d in rows[a]:
        nome=str(d.get('nome',b[:-3])).replace('|','/')
        out.append(f"| [{nome}]({a}/{b}) | {d.get('status','—')} | {d.get('nota','—')} | {str(d.get('onde_entrou','—')).replace('|','/')} |")
    out.append('')
import csv
apoio=[(a,os.path.basename(x)) for a in areas for x in sorted(glob.glob(f'{root}/{a}/*.md')) if os.path.basename(x).startswith(('ferramentas-','colecoes-'))]
for a in areas:
    with open(f'{root}/{a}/_lista.tsv','w',encoding='utf-8',newline='') as fh:
        w=csv.writer(fh,delimiter='\t')
        w.writerow(['arquivo','nome','repositorio','status','nota','onde_entrou','categoria_nossa'])
        for b,d in rows[a]:
            w.writerow([b]+[str(d.get(k,'')).replace('\t',' ').replace('\n',' ') for k in ('nome','repositorio','status','nota','onde_entrou','categoria_nossa')])
    c=collections.Counter(str(d.get('status')) for b,d in rows[a])
    p=f'{root}/{a}/LEIA-ME.md'
    t=open(p,encoding='utf-8').read()
    t=re.sub(r'^\*\*Status atual \(.*$\n?','',t,flags=re.M)
    t=t.rstrip('\n')+f"\n\n**Status atual ({len(rows[a])} fichas, atualizado após o PR #21 de 12/10/2026):** "+', '.join(f'{k} {v}' for k,v in sorted(c.items(),key=lambda x:-x[1]))+".\n"
    open(p,'w',encoding='utf-8').write(t)
if apoio:
    out+=['## Documentos de apoio (não são fichas de skill)','']+[f'- [{b}]({a}/{b})' for a,b in apoio]+['']
open(f'{root}/INDICE.md','w',encoding='utf-8').write('\n'.join(out))
print('fichas',{a:len(v) for a,v in rows.items()}); print('status',dict(st)); print('problemas',len(problems))
for p in problems[:40]: print(p)
