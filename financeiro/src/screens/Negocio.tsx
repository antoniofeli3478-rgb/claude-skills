import { useMemo, useState } from "react";
import { Boxes, Pencil, Plus } from "lucide-react";
import { addDias, br, inicioSemana, mesDe, nomeMesCurto, anoDe } from "../core/dates";
import { brl, type Cents } from "../core/money";
import { disponivelParaRetirada, indicadores, ritmoVendas, serieMensal, serieSemanal, simular } from "../core/negocio";
import { totaisVenda, ordenar } from "../core/ledger";
import type { CompraEstoque, Produto, Venda } from "../core/types";
import { useApp } from "../app/store";
import { useAcoes } from "../app/acoes";
import { ir } from "../app/rotas";
import { db } from "../data/db";
import { novoId } from "../data/repo";
import { Barras } from "../ui/graficos";
import { Aviso, Badge, Button, Campo, Card, Chips, cn, Dinheiro, Folha, Interruptor, Money, Numero, Secao, Segmentos, Stat, Texto, Vazio, useUi } from "../ui/ui";

type Aba = "resumo" | "vendas" | "produtos" | "compras" | "simulacao" | "retirada";

export function Negocio({ aba: abaRota }: { aba: string }) {
  const aba = (["resumo", "vendas", "produtos", "compras", "simulacao", "retirada"].includes(abaRota) ? abaRota : "resumo") as Aba;
  return (
    <div className="flex flex-col gap-5">
      <h1 className="font-display text-[28px] font-semibold tracking-tight">Negócio NFC</h1>
      <Chips value={aba} onChange={(v) => ir("negocio", v)} opcoes={[{ valor: "resumo", rotulo: "Resumo" }, { valor: "vendas", rotulo: "Vendas" }, { valor: "produtos", rotulo: "Produtos e estoque" }, { valor: "compras", rotulo: "Compras" }, { valor: "simulacao", rotulo: "Simulação" }, { valor: "retirada", rotulo: "Retirada pessoal" }]} />
      {aba === "resumo" && <Resumo />}
      {aba === "vendas" && <Vendas />}
      {aba === "produtos" && <Produtos />}
      {aba === "compras" && <Compras />}
      {aba === "simulacao" && <Simulacao />}
      {aba === "retirada" && <Retirada />}
    </div>
  );
}

function Resumo() {
  const { d, sit, hoje } = useApp();
  const [per, setPer] = useState<"semana" | "mes" | "ano">("mes");
  const ini = per === "semana" ? inicioSemana(hoje) : per === "mes" ? `${mesDe(hoje)}-01` : `${anoDe(hoje)}-01-01`;
  const x = useMemo(() => indicadores(d, sit, ini, per === "semana" ? addDias(ini, 6) : hoje), [d, sit, ini, per, hoje]);
  const semanal = useMemo(() => serieSemanal(d, sit, hoje, 8), [d, sit, hoje]);
  const mensal = useMemo(() => serieMensal(d, sit, hoje, 6), [d, sit, hoje]);
  const [serie, setSerie] = useState<"semanas" | "meses">("semanas");
  const ret = disponivelParaRetirada(d, sit);
  if (!d.produtos.length) return <Vazio titulo="Cadastre seus produtos" icone={<Boxes />} acao={<Button onClick={() => ir("negocio", "produtos")}>Cadastrar produto</Button>}>Com o custo e o preço de cada placa, o app separa faturamento, custo, lucro e o que você pode tirar para você.</Vazio>;
  return (
    <div className="flex flex-col gap-5">
      <Segmentos value={per} onChange={setPer} opcoes={[{ valor: "semana", rotulo: "Esta semana" }, { valor: "mes", rotulo: "Este mês" }, { valor: "ano", rotulo: "Este ano" }]} />
      <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
        <Stat rotulo="Faturamento" valor={<Money c={x.faturamento} />} dica={`${x.vendas} venda(s) · ${x.unidades} placa(s)`} />
        <Stat rotulo="Recebido de fato" valor={<Money c={x.recebido} />} dica="entrou no caixa no período" />
        <Stat rotulo="Custo das placas (CMV)" valor={<Money c={x.cmv} />} dica={x.cmvEstimado ? "parte estimada pelo custo de referência" : "pelo custo médio do estoque"} />
        <Stat rotulo="Lucro bruto" valor={<Money c={x.lucroBruto} />} dica="faturamento − custo" />
        <Stat rotulo="Despesas do negócio" valor={<Money c={x.despesasOperacionais} />} dica="transporte, embalagem, taxas" />
        <Stat rotulo="Lucro líquido" valor={<Money c={x.lucroLiquido} />} tom={x.lucroLiquido < 0 ? "bad" : x.lucroLiquido > 0 ? "good" : undefined} dica={x.margem != null ? `margem de ${Math.round(x.margem * 100)}%` : "sem vendas"} />
        <Stat rotulo="A receber (total)" valor={<Money c={sit.capitalNegocio.aReceber} />} dica="vendas ainda não pagas" />
        <Stat rotulo="Compras de estoque" valor={<Money c={x.comprasEstoque} />} dica="não é despesa: vira estoque" />
      </div>
      <Aviso tom="info">Faturamento é o que você vendeu. Recebido é o que entrou. Lucro é o que sobra depois do custo das placas e das despesas. Só o recebido está no caixa.</Aviso>

      <Secao titulo="Evolução" acao={<Segmentos className="w-48" value={serie} onChange={setSerie} opcoes={[{ valor: "semanas", rotulo: "Semanas" }, { valor: "meses", rotulo: "Meses" }]} />}>
        <Card className="p-4">
          <Barras dados={serie === "semanas" ? semanal.map((s) => ({ rotulo: br(s.ini).slice(0, 5), valores: [s.faturamento, s.lucro] })) : mensal.map((s) => ({ rotulo: nomeMesCurto(s.key), valores: [s.faturamento, s.lucro] }))} series={[{ nome: "Faturamento", cor: "#5aa9e6" }, { nome: "Lucro líquido", cor: "var(--good)" }]} />
        </Card>
      </Secao>

      <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
        <Secao titulo="Capital do negócio">
          <Card className="flex flex-col gap-2 p-4 text-[14px]">
            <div className="flex justify-between"><span className="text-muted">Caixa (envelope do negócio)</span><Money c={sit.capitalNegocio.caixa} className="font-semibold" /></div>
            <div className="flex justify-between"><span className="text-muted">Estoque a preço de custo</span><Money c={sit.capitalNegocio.estoque} className="font-semibold" /></div>
            <div className="flex justify-between border-t border-line pt-2"><span className="font-semibold">Total investido no negócio</span><Money c={sit.capitalNegocio.total} className="font-semibold" /></div>
            <div className="flex justify-between"><span className="text-muted">Para repor o estoque mínimo</span><Money c={sit.reposicao.total} className="font-semibold" /></div>
            <div className="flex justify-between"><span className="text-muted">Pode retirar para você</span><Money c={ret.valor} className="font-semibold text-accent" /></div>
            {ret.provisorio.length > 0 && <div className="text-[12.5px] text-warn">Estimativa provisória: {ret.provisorio.join(" ")}</div>}
          </Card>
        </Secao>
        <Secao titulo="Por produto no período">
          <Card className="divide-y divide-line">
            {d.produtos.map((p) => { const pp = x.porProduto[p.id]; return (
              <div key={p.id} className="flex items-center justify-between gap-3 px-4 py-3 text-[14px]">
                <span className="min-w-0"><span className="block truncate font-semibold">{p.nome}</span><span className="text-[12.5px] text-muted">{pp?.unidades ?? 0} vendida(s) · {sit.estoque.porProduto[p.id]?.qtd ?? 0} em estoque</span></span>
                <span className="text-right"><Money c={pp?.faturamento ?? 0} className="block font-semibold" /><span className="text-[12.5px] text-muted">lucro bruto <Money c={pp?.lucroBruto ?? 0} /></span></span>
              </div>
            ); })}
          </Card>
        </Secao>
      </div>
    </div>
  );
}

function Vendas() {
  const { d, sit } = useApp();
  const ac = useAcoes();
  const [filtro, setFiltro] = useState<"todas" | "receber" | "canceladas">("todas");
  const vendas = useMemo(() => ordenar(d.movimentos).reverse().filter((m): m is Venda => m.tipo === "venda").filter((v) => {
    const t = totaisVenda(v);
    return filtro === "todas" ? !v.cancelada : filtro === "receber" ? t.aReceber > 0 : v.cancelada;
  }), [d.movimentos, filtro]);
  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <Chips value={filtro} onChange={setFiltro} opcoes={[{ valor: "todas", rotulo: "Todas" }, { valor: "receber", rotulo: "A receber" }, { valor: "canceladas", rotulo: "Canceladas" }]} />
        <Button tamanho="sm" onClick={() => ac.nova({ tipo: "venda" })}><Plus size={16} />Nova venda</Button>
      </div>
      {vendas.length ? (
        <Card className="divide-y divide-line">
          {vendas.map((v) => {
            const t = totaisVenda(v);
            const cmv = sit.estoque.cmvVenda[v.id] ?? 0;
            const r = sit.aReceber.find((x) => x.movId === v.id);
            return (
              <div key={v.id} className="flex items-center gap-3 px-4 py-3">
                <button className="min-w-0 flex-1 text-left" onClick={() => ac.nova({ editId: v.id })}>
                  <div className="flex items-center gap-1.5"><span className="truncate font-semibold">{v.cliente || "Venda"}</span>{t.status === "pendente" && <Badge tom="warn">a receber</Badge>}{t.status === "parcial" && <Badge tom="warn">parcial</Badge>}{t.status === "cancelada" && <Badge>cancelada</Badge>}</div>
                  <div className="text-[12.5px] text-muted">{br(v.data)} · {v.qtd} × {d.produtos.find((p) => p.id === v.produtoId)?.nome} · {v.forma}{!v.cancelada && ` · lucro bruto ${brl(t.bruto - cmv)}`}</div>
                </button>
                <div className="text-right"><Money c={t.bruto} className="block font-semibold" />{t.aReceber > 0 && <span className="text-[12px] text-warn">falta {brl(t.aReceber)}</span>}</div>
                {r && <Button variante="soft" tamanho="sm" onClick={() => ac.receber(r)}>Receber</Button>}
              </div>
            );
          })}
        </Card>
      ) : <Vazio titulo="Nenhuma venda aqui" acao={<Button onClick={() => ac.nova({ tipo: "venda" })}>Registrar venda</Button>} />}
    </div>
  );
}

function Produtos() {
  const { d, sit } = useApp();
  const ac = useAcoes();
  const [editar, setEditar] = useState<Produto | "novo" | null>(null);
  return (
    <div className="flex flex-col gap-4">
      <div className="flex justify-end"><Button tamanho="sm" onClick={() => setEditar("novo")}><Plus size={16} />Novo produto</Button></div>
      {d.produtos.length ? (
        <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
          {d.produtos.map((p) => {
            const e = sit.estoque.porProduto[p.id];
            const custo = e?.custoMedio || p.custoReferencia;
            const margem = p.precoVenda - custo - p.custoAdicional;
            const baixo = p.estoqueMinimo > 0 && (e?.qtd ?? 0) < p.estoqueMinimo;
            return (
              <Card key={p.id} className={cn("flex flex-col gap-3 p-4", !p.ativo && "opacity-60")}>
                <div className="flex items-start justify-between gap-2">
                  <div><div className="font-semibold">{p.nome}</div><div className="text-[12.5px] text-muted">{p.categoria}{p.fornecedor ? ` · ${p.fornecedor}` : ""}{!p.ativo ? " · inativo" : ""}</div></div>
                  <button aria-label="Editar produto" onClick={() => setEditar(p)} className="grid h-9 w-9 place-items-center rounded-lg text-muted hover:bg-surface-2"><Pencil size={16} /></button>
                </div>
                <div className="grid grid-cols-3 gap-2 text-center">
                  <div className={cn("rounded-xl p-2", baixo ? "bg-warn-soft" : "bg-surface-2")}><div className="text-[11.5px] text-muted">Estoque</div><div className={cn("num text-[18px] font-semibold", baixo && "text-warn", (e?.qtd ?? 0) < 0 && "text-bad")}>{e?.qtd ?? 0}</div><div className="text-[11px] text-muted">mín. {p.estoqueMinimo}</div></div>
                  <div className="rounded-xl bg-surface-2 p-2"><div className="text-[11.5px] text-muted">Custo médio</div><div className="num text-[15px] font-semibold">{brl(custo)}</div><div className="text-[11px] text-muted">{e?.custoMedio ? "do estoque" : "referência"}</div></div>
                  <div className="rounded-xl bg-surface-2 p-2"><div className="text-[11.5px] text-muted">Preço</div><div className="num text-[15px] font-semibold">{brl(p.precoVenda)}</div><div className={cn("text-[11px]", margem < 0 ? "text-bad" : "text-good")}>lucro {brl(margem)}</div></div>
                </div>
                {e?.negativo && <Aviso tom="bad">Você registrou mais vendas do que entradas de estoque. Registre a compra ou o estoque inicial.</Aviso>}
                <div className="flex gap-2"><Button variante="soft" tamanho="sm" onClick={() => ac.nova({ tipo: "estoque", produtoId: p.id })}>Comprar estoque</Button><Button variante="outline" tamanho="sm" onClick={() => ac.nova({ tipo: "venda", produtoId: p.id })}>Vender</Button></div>
              </Card>
            );
          })}
        </div>
      ) : <Vazio titulo="Nenhum produto ainda" acao={<Button onClick={() => setEditar("novo")}>Cadastrar produto</Button>}>Ex.: Placa NFC Google Avaliações, Placa Pix, Placa Instagram, Cardápio.</Vazio>}
      {editar && <EditarProduto produto={editar === "novo" ? null : editar} onFechar={() => setEditar(null)} />}
    </div>
  );
}

export function EditarProduto({ produto, onFechar, sugestao }: { produto: Produto | null; onFechar: () => void; sugestao?: Partial<Produto> }) {
  const { d, hoje } = useApp();
  const { toast, confirmar } = useUi();
  const s = { ...sugestao, ...produto };
  const [nome, setNome] = useState(s.nome ?? "");
  const [categoria, setCategoria] = useState(s.categoria ?? "Google");
  const [preco, setPreco] = useState<Cents | null>(s.precoVenda ?? null);
  const [custo, setCusto] = useState<Cents | null>(s.custoReferencia ?? null);
  const [adicional, setAdicional] = useState<Cents | null>(s.custoAdicional ?? 0);
  const [minimo, setMinimo] = useState(s.estoqueMinimo ?? 0);
  const [fornecedor, setFornecedor] = useState(s.fornecedor ?? "");
  const [ativo, setAtivo] = useState(s.ativo ?? true);
  const salvar = async () => {
    if (!nome.trim() || preco == null || custo == null) { toast("Preencha nome, preço e custo.", true); return; }
    const hist = [...(produto?.custoHistorico ?? [])];
    if (!produto || produto.custoReferencia !== custo) hist.push({ data: hoje, custo });
    await db().produtos.put({ id: produto?.id ?? novoId(), nome: nome.trim(), categoria, precoVenda: preco, custoReferencia: custo, custoHistorico: hist, custoAdicional: adicional ?? 0, estoqueMinimo: minimo, fornecedor, ativo });
    toast("Produto salvo");
    onFechar();
  };
  const apagar = async () => {
    if (!produto) return;
    if (d.movimentos.some((m) => (m.tipo === "venda" || m.tipo === "estoque_compra") && m.produtoId === produto.id)) { toast("Este produto tem vendas ou compras. Desative em vez de apagar.", true); return; }
    if (!(await confirmar({ titulo: "Apagar produto?", botao: "Apagar", perigo: true }))) return;
    await db().produtos.delete(produto.id);
    onFechar();
  };
  return (
    <Folha aberta onFechar={onFechar} titulo={produto ? "Editar produto" : "Novo produto"} rodape={<div className="flex gap-2">{produto && <Button variante="outline" className="text-bad" onClick={apagar}>Apagar</Button>}<Button className="flex-1" onClick={salvar}>Salvar</Button></div>}>
      <div className="flex flex-col gap-4">
        <Campo rotulo="Nome"><Texto value={nome} onChange={(e) => setNome(e.target.value)} placeholder="Ex.: Placa NFC Google Avaliações" /></Campo>
        <div className="grid grid-cols-2 gap-3">
          <Campo rotulo="Categoria"><Texto value={categoria} onChange={(e) => setCategoria(e.target.value)} placeholder="Google, Pix, Instagram, Cardápio" /></Campo>
          <Campo rotulo="Fornecedor"><Texto value={fornecedor} onChange={(e) => setFornecedor(e.target.value)} placeholder="Opcional" /></Campo>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <Campo rotulo="Preço de venda" dica="Ex.: 80,00 ou 89,90"><Dinheiro value={preco} onChange={setPreco} /></Campo>
          <Campo rotulo="Custo por unidade" dica="Ex.: 14,00 ou 11,67 em lote maior"><Dinheiro value={custo} onChange={setCusto} /></Campo>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <Campo rotulo="Custo adicional por unidade" dica="Gravação, embalagem"><Dinheiro value={adicional} onChange={setAdicional} /></Campo>
          <Campo rotulo="Estoque mínimo" dica="Abaixo disso o app avisa"><Numero value={minimo} onChange={setMinimo} /></Campo>
        </div>
        <label className="flex items-center justify-between gap-3"><span className="text-[14px] font-semibold">Ativo</span><Interruptor ligado={ativo} onChange={setAtivo} rotulo="Ativo" /></label>
        {produto && produto.custoHistorico.length > 0 && (
          <div className="flex flex-col gap-1 rounded-xl bg-surface-2 p-3 text-[13px]">
            <div className="font-semibold text-muted">Histórico do custo</div>
            {[...produto.custoHistorico].reverse().map((h, i) => <div key={i} className="flex justify-between"><span>{br(h.data)}</span><Money c={h.custo} /></div>)}
          </div>
        )}
        <Aviso tom="info">O custo das vendas usa o custo médio do estoque que você comprou. O custo por unidade daqui serve para simulações e quando não há estoque registrado.</Aviso>
      </div>
    </Folha>
  );
}

function Compras() {
  const { d, sit } = useApp();
  const ac = useAcoes();
  const compras = ordenar(d.movimentos).reverse().filter((m): m is CompraEstoque => m.tipo === "estoque_compra");
  return (
    <div className="flex flex-col gap-4">
      <div className="flex justify-end"><Button tamanho="sm" onClick={() => ac.nova({ tipo: "estoque" })}><Plus size={16} />Nova compra</Button></div>
      {compras.length ? (
        <Card className="divide-y divide-line">
          {compras.map((c) => {
            const comp = sit.compromissos.find((x) => x.movId === c.id);
            return (
              <div key={c.id} className="flex items-center gap-3 px-4 py-3">
                <button className="min-w-0 flex-1 text-left" onClick={() => ac.nova({ editId: c.id })}>
                  <div className="flex items-center gap-1.5"><span className="truncate font-semibold">{c.descricao}</span>{c.status === "pendente" && <Badge tom="warn">a pagar</Badge>}{c.status === "inicial" && <Badge>estoque inicial</Badge>}</div>
                  <div className="text-[12.5px] text-muted">{br(c.data)} · {c.qtd} un. a {brl(Math.round(c.valor / c.qtd))}{c.fornecedor ? ` · ${c.fornecedor}` : ""}{c.status === "pendente" && c.vencimento ? ` · vence ${br(c.vencimento)}` : ""}</div>
                </button>
                <Money c={c.valor} className="font-semibold" />
                {comp && <Button variante="soft" tamanho="sm" onClick={() => ac.pagarConta(comp)}>Pagar</Button>}
              </div>
            );
          })}
        </Card>
      ) : <Vazio titulo="Nenhuma compra de estoque" acao={<Button onClick={() => ac.nova({ tipo: "estoque" })}>Registrar compra</Button>}>Ao registrar uma compra, o estoque aumenta e o dinheiro sai do envelope do negócio (ou vira conta a pagar).</Vazio>}
    </div>
  );
}

function Simulacao() {
  const { d, sit, hoje } = useApp();
  const ritmo = ritmoVendas(d, hoje);
  const p = d.produtos.find((x) => x.ativo) ?? d.produtos[0];
  const pc = sit.metas.find((m) => m.envelope.tipo === "pc");
  const estNfc = d.config.estrategias.find((e) => e.id === d.config.fontes.find((f) => f.id === "nfc")?.estrategiaId);
  const pctPC = estNfc?.regras.find((r) => r.envelopeId === pc?.envelope.id)?.valor ?? 40;
  const [u, setU] = useState(Math.max(1, Math.round(ritmo?.unidadesSemana ?? 5)));
  const [preco, setPreco] = useState<Cents | null>(ritmo?.precoMedio || p?.precoVenda || 8000);
  const [custo, setCusto] = useState<Cents | null>(p ? (sit.estoque.porProduto[p.id]?.custoMedio || p.custoReferencia) : 1400);
  const [adic, setAdic] = useState<Cents | null>(p?.custoAdicional ?? 0);
  const [desp, setDesp] = useState<Cents | null>(3000);
  const [semanas, setSemanas] = useState(4);
  const [prazo, setPrazo] = useState(0);
  const [pct, setPct] = useState(pctPC);
  const estoqueAtual = Object.values(sit.estoque.porProduto).reduce((a, e) => a + Math.max(0, e.qtd), 0);
  const r = simular({ unidadesSemana: u, preco: preco ?? 0, custoUnit: custo ?? 0, custoAdicional: adic ?? 0, despesasSemana: desp ?? 0, semanas, prazoRecebimentoDias: prazo, pctPatrimonio: pct, estoqueAtual, faltaMeta: pc?.falta ?? null });
  return (
    <div className="flex flex-col gap-5">
      <p className="text-[14px] text-muted">"Se eu vender X placas por semana, quanto gero de lucro e quanto vai para o meu patrimônio?" Os campos já vêm com seus números reais{ritmo ? ` (você vendeu em média ${ritmo.unidadesSemana.toFixed(1).replace(".", ",")} por semana)` : ""}. Mude à vontade.</p>
      <Card className="grid grid-cols-2 gap-3 p-4 sm:grid-cols-4">
        <Campo rotulo="Placas por semana"><Numero value={u} onChange={setU} /></Campo>
        <Campo rotulo="Preço real de venda"><Dinheiro value={preco} onChange={setPreco} /></Campo>
        <Campo rotulo="Custo por placa"><Dinheiro value={custo} onChange={setCusto} /></Campo>
        <Campo rotulo="Custo adicional"><Dinheiro value={adic} onChange={setAdic} /></Campo>
        <Campo rotulo="Despesas por semana" dica="Transporte, etc."><Dinheiro value={desp} onChange={setDesp} /></Campo>
        <Campo rotulo="Prazo para receber (dias)"><Numero value={prazo} onChange={setPrazo} /></Campo>
        <Campo rotulo="Semanas simuladas"><Numero value={semanas} onChange={(n) => setSemanas(Math.max(1, Math.min(52, n)))} /></Campo>
        <Campo rotulo="% do lucro para o patrimônio"><Numero value={pct} onChange={(n) => setPct(Math.max(0, Math.min(100, n)))} /></Campo>
      </Card>
      <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
        <Stat rotulo="Lucro por placa" valor={<Money c={r.margemUnit} />} tom={r.margemUnit < 0 ? "bad" : undefined} />
        <Stat rotulo="Lucro por semana" valor={<Money c={r.lucroSemana} />} dica="depois das despesas" tom={r.lucroSemana < 0 ? "bad" : "good"} />
        <Stat rotulo={`Lucro em ${semanas} semana(s)`} valor={<Money c={r.total.lucro} />} dica={`faturando ${brl(r.total.faturamento)}`} />
        <Stat rotulo="Para o patrimônio" valor={<Money c={r.total.paraPatrimonio} />} dica={`${brl(r.paraPatrimonioSemana)} por semana`} tom="accent" />
        <Stat rotulo="Gasto para repor estoque" valor={<Money c={r.total.reposicao} />} dica="no período" />
        <Stat rotulo="Capital de giro necessário" valor={<Money c={r.capitalDeGiro} />} dica="para não parar de vender" />
        <Stat rotulo="Estoque atual dura" valor={r.estoqueDuraSemanas == null ? "—" : `${r.estoqueDuraSemanas} sem.`} dica={`${estoqueAtual} placa(s) em estoque`} />
        <Stat rotulo="PC nesse ritmo" valor={r.semanasMeta == null ? "—" : `${r.semanasMeta} sem.`} dica={pc?.meta ? `faltam ${brl(pc.falta)}` : "defina a meta do PC"} />
      </div>
      {r.avisos.map((a) => <Aviso key={a} tom="warn">{a}</Aviso>)}
      <p className="text-[12.5px] text-muted">Premissas: preço, custo e despesas constantes; todo o lucro do período é retirado e {pct}% dele vai para o patrimônio. É uma conta, não uma promessa: semanas fracas acontecem.</p>
    </div>
  );
}

function Retirada() {
  const { d, sit } = useApp();
  const ac = useAcoes();
  const r = disponivelParaRetirada(d, sit);
  const retiradas = ordenar(d.movimentos).reverse().filter((m) => m.tipo === "envelope" && m.origem === "retirada").slice(0, 10);
  const [minimo, setMinimo] = useState<Cents | null>(sit.env.negocio?.minimo ?? null);
  const { toast } = useUi();
  return (
    <div className="flex flex-col gap-5">
      <Card className="flex flex-col gap-3 p-5">
        <div className="text-[14px] text-muted">Você pode tirar sem comprometer o negócio</div>
        <div className="font-display text-[40px] font-semibold leading-none num text-accent">{brl(r.valor)}</div>
        <div className="grid grid-cols-[minmax(0,1fr)_auto] gap-x-3 gap-y-1.5 text-[14px]">
          <span className="text-muted">Caixa do negócio</span><Money c={r.caixa} className="text-right" />
          <span className="text-muted">− Contas do negócio a pagar</span><Money c={-r.contas} className="text-right" />
          <span className="text-muted">− Reposição do estoque mínimo</span><Money c={-r.reposicao} className="text-right" />
          <span className="text-muted">− Capital de giro mínimo</span><Money c={-r.minimo} className="text-right" />
        </div>
        {r.provisorio.length > 0 && <Aviso tom="warn">Estimativa provisória: {r.provisorio.join(" ")}</Aviso>}
        <Button onClick={() => ac.nova({ tipo: "retirada" })} disabled={r.caixa <= 0}>Fazer retirada</Button>
        <p className="text-[12.5px] text-muted">A retirada tira dinheiro do envelope do negócio e coloca em "a distribuir". Em seguida você distribui entre contas, PC, reserva e dinheiro livre.</p>
      </Card>
      <Secao titulo="Capital de giro mínimo">
        <Card className="flex flex-wrap items-end gap-3 p-4">
          <Campo rotulo="Valor que nunca deve sair do negócio" className="min-w-[200px] flex-1"><Dinheiro value={minimo} onChange={setMinimo} /></Campo>
          <Button variante="outline" onClick={async () => { if (sit.env.negocio) { await db().envelopes.update(sit.env.negocio.id, { minimo: minimo ?? 0 }); toast("Capital de giro mínimo salvo"); } }}>Salvar</Button>
        </Card>
      </Secao>
      {retiradas.length > 0 && (
        <Secao titulo="Últimas retiradas">
          <Card className="divide-y divide-line">{retiradas.map((m) => <button key={m.id} onClick={() => ac.nova({ editId: m.id })} className="flex w-full justify-between px-4 py-3 text-[14px]"><span>{br(m.data)}</span><Money c={m.tipo === "envelope" ? -m.itens[0].valor : 0} className="font-semibold" /></button>)}</Card>
        </Secao>
      )}
    </div>
  );
}

