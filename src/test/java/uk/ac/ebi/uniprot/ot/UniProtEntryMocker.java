package uk.ac.ebi.uniprot.ot;

import uk.ac.ebi.kraken.ffwriter.LineType;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.parser.UniprotLineParser;
import uk.ac.ebi.uniprot.parser.impl.DefaultUniprotLineParserFactory;
import uk.ac.ebi.uniprot.parser.impl.ac.AcLineObject;
import uk.ac.ebi.uniprot.parser.impl.cc.CcLineObject;
import uk.ac.ebi.uniprot.parser.impl.de.DeLineObject;
import uk.ac.ebi.uniprot.parser.impl.dr.DrLineObject;
import uk.ac.ebi.uniprot.parser.impl.dt.DtLineObject;
import uk.ac.ebi.uniprot.parser.impl.entry.EntryObject;
import uk.ac.ebi.uniprot.parser.impl.entry.EntryObjectConverter;
import uk.ac.ebi.uniprot.parser.impl.ft.FtLineObject;
import uk.ac.ebi.uniprot.parser.impl.gn.GnLineObject;
import uk.ac.ebi.uniprot.parser.impl.id.IdLineObject;
import uk.ac.ebi.uniprot.parser.impl.kw.KwLineObject;
import uk.ac.ebi.uniprot.parser.impl.oc.OcLineObject;
import uk.ac.ebi.uniprot.parser.impl.og.OgLineObject;
import uk.ac.ebi.uniprot.parser.impl.oh.OhLineObject;
import uk.ac.ebi.uniprot.parser.impl.os.OsLineObject;
import uk.ac.ebi.uniprot.parser.impl.ox.OxLineObject;
import uk.ac.ebi.uniprot.parser.impl.pe.PeLineObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created 28/11/16
 * @author Edd
 */
public class UniProtEntryMocker {
    private final static String DEFAULT_ENTRY =
            "ID   CR2AA_BACTK             Reviewed;         633 AA.\n" +
                    "AC   P0A377; O52764; P21253;\n" +
                    "DT   15-MAR-2005, integrated into UniProtKB/Swiss-Prot.\n" +
                    "DT   15-MAR-2005, sequence version 1.\n" +
                    "DT   26-NOV-2014, entry version 43.\n" +
                    "DE   RecName: Full=Pesticidal crystal protein cry2Aa;\n" +
                    "DE   AltName: Full=71 kDa crystal protein;\n" +
                    "DE   AltName: Full=Crystaline entomocidal protoxin;\n" +
                    "DE   AltName: Full=Insecticidal delta-endotoxin CryIIA(a);\n" +
                    "DE   AltName: Full=Mosquito factor;\n" +
                    "DE   AltName: Full=P2 crystal protein;\n" +
                    "GN   Name=cry2Aa; Synonyms=cryB1, cryII, cryIIA(a);\n" +
                    "OS   Bacillus thuringiensis subsp. kurstaki.\n" +
                    "OC   Bacteria; Firmicutes; Bacilli; Bacillales; Bacillaceae; Bacillus;\n" +
                    "OC   Bacillus cereus group.\n" +
                    "OX   NCBI_TaxID=29339;\n" +
                    "RN   [1]\n" +
                    "RP   NUCLEOTIDE SEQUENCE [GENOMIC DNA].\n" +
                    "RC   STRAIN=HD-1-Dippel;\n" +
                    "RX   PubMed=2914879;\n" +
                    "RA   Widner W.R., Whiteley H.R.;\n" +
                    "RT   \"Two highly related insecticidal crystal proteins of Bacillus\n" +
                    "RT   thuringiensis subsp. kurstaki possess different host range\n" +
                    "RT   specificities.\";\n" +
                    "RL   J. Bacteriol. 171:965-974(1989).\n" +
                    "RN   [2]\n" +
                    "RP   NUCLEOTIDE SEQUENCE [GENOMIC DNA], AND PROTEIN SEQUENCE OF 1-26.\n" +
                    "RC   STRAIN=HD-1-Dippel, and HD-263;\n" +
                    "RX   PubMed=3121615;\n" +
                    "RA   Donovan W.P., Dankocsik C.C., Gilbert M.P., Gawron-Burke M.C.,\n" +
                    "RA   Groat R.G., Carlton B.C.;\n" +
                    "RT   \"Amino acid sequence and entomocidal activity of the P2 crystal\n" +
                    "RT   protein. An insect toxin from Bacillus thuringiensis var. kurstaki.\";\n" +
                    "RL   J. Biol. Chem. 263:561-567(1988).\n" +
                    "RN   [3]\n" +
                    "RP   ERRATUM, AND SEQUENCE REVISION.\n" +
                    "RA   Donovan W.P., Dankocsik C.C., Gilbert M.P., Gawron-Burke M.C.,\n" +
                    "RA   Groat R.G., Carlton B.C.;\n" +
                    "RL   J. Biol. Chem. 264:4740-4740(1989).\n" +
                    "RN   [4]\n" +
                    "RP   X-RAY CRYSTALLOGRAPHY (2.2 ANGSTROMS).\n" +
                    "RX   PubMed=11377201; DOI=10.1016/S0969-2126(01)00601-3;\n" +
                    "RA   Morse R.J., Yamamoto T., Stroud R.M.;\n" +
                    "RT   \"Structure of Cry2Aa suggests an unexpected receptor binding\n" +
                    "RT   epitope.\";\n" +
                    "RL   Structure 9:409-417(2001).\n" +
                    "CC   -!- FUNCTION: Promotes colloidosmotic lysis by binding to the midgut\n" +
                    "CC       epithelial cells of both dipteran (Aedes aegypti) and lepidopteran\n" +
                    "CC       (Manduca sexta) larvae.\n" +
                    "CC   -!- DEVELOPMENTAL STAGE: The crystal protein is produced during\n" +
                    "CC       sporulation and is accumulated both as an inclusion and as part of\n" +
                    "CC       the spore coat.\n" +
                    "CC   -!- MISCELLANEOUS: Toxic segment of the protein is located in the N-\n" +
                    "CC       terminus.\n" +
                    "CC   -!- SIMILARITY: Belongs to the delta endotoxin family. {ECO:0000305}.\n" +
                    "DR   EMBL; M23723; AAA83516.1; -; Genomic_DNA.\n" +
                    "DR   EMBL; M31738; AAA22335.1; -; Genomic_DNA.\n" +
                    "DR   PIR; C32053; C32053.\n" +
                    "DR   PDB; 1I5P; X-ray; 2.20 A; A=1-633.\n" +
                    "DR   PDBsum; 1I5P; -.\n" +
                    "DR   ProteinModelPortal; P0A377; -.\n" +
                    "DR   SMR; P0A377; 1-633.\n" +
                    "DR   EvolutionaryTrace; P0A377; -.\n" +
                    "DR   GO; GO:0006952; P:defense response; IEA:InterPro.\n" +
                    "DR   GO; GO:0009405; P:pathogenesis; IEA:InterPro.\n" +
                    "DR   GO; GO:0030435; P:sporulation resulting in formation of a cellular spore; IEA:UniProtKB-KW" +
                    ".\n" +
                    "DR   Gene3D; 1.20.190.10; -; 1.\n" +
                    "DR   Gene3D; 2.100.10.10; -; 1.\n" +
                    "DR   Gene3D; 2.60.120.260; -; 1.\n" +
                    "DR   InterPro; IPR005638; Endotoxin_C.\n" +
                    "DR   InterPro; IPR001178; Endotoxin_cen_dom.\n" +
                    "DR   InterPro; IPR015214; Endotoxin_cen_dom_subgr2.\n" +
                    "DR   InterPro; IPR005639; Endotoxin_N.\n" +
                    "DR   InterPro; IPR008979; Galactose-bd-like.\n" +
                    "DR   Pfam; PF03944; Endotoxin_C; 1.\n" +
                    "DR   Pfam; PF09131; Endotoxin_mid; 1.\n" +
                    "DR   Pfam; PF03945; Endotoxin_N; 1.\n" +
                    "DR   ProDom; PD579378; Endotoxin_M_sub_2; 1.\n" +
                    "DR   SUPFAM; SSF49785; SSF49785; 1.\n" +
                    "DR   SUPFAM; SSF51096; SSF51096; 1.\n" +
                    "DR   SUPFAM; SSF56849; SSF56849; 1.\n" +
                    "PE   1: Evidence at protein level;\n" +
                    "KW   3D-structure; Direct protein sequencing; Sporulation; Toxin.\n" +
                    "FT   CHAIN         1    633       Pesticidal crystal protein cry2Aa.\n" +
                    "FT                                /FTId=PRO_0000174055.\n" +
                    "FT   VARIANT       1      1       Missing (in 50% of the molecules).\n" +
                    "FT   TURN         16     18       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX        25     28       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX        31     44       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX        53     65       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX        74     81       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX        83     85       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX        88    101       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       107    135       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      138    140       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       145    160       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       161    164       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       170    193       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       195    198       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       202    232       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      235    237       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       238    251       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       253    259       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   TURN        260    263       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      265    268       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      275    278       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      281    283       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      287    289       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       290    292       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       293    300       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   TURN        301    305       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      308    321       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      328    342       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       343    345       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      354    356       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      360    363       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   TURN        368    370       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      373    377       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      381    385       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   TURN        386    388       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      389    393       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      397    403       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      405    409       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      422    432       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       435    438       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      462    469       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      474    477       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      481    486       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      489    491       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      493    496       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      502    506       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   HELIX       507    509       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      510    513       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      516    520       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      522    525       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      527    537       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      542    550       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      555    561       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      564    571       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      574    577       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      588    596       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      601    610       {ECO:0000244|PDB:1I5P}.\n" +
                    "FT   STRAND      617    626       {ECO:0000244|PDB:1I5P}.\n" +
                    "SQ   SEQUENCE   633 AA;  70852 MW;  15182F4C778E58A4 CRC64;\n" +
                    "     MNNVLNSGRT TICDAYNVVA HDPFSFEHKS LDTIQKEWME WKRTDHSLYV APVVGTVSSF\n" +
                    "     LLKKVGSLIG KRILSELWGI IFPSGSTNLM QDILRETEQF LNQRLNTDTL ARVNAELIGL\n" +
                    "     QANIREFNQQ VDNFLNPTQN PVPLSITSSV NTMQQLFLNR LPQFQIQGYQ LLLLPLFAQA\n" +
                    "     ANMHLSFIRD VILNADEWGI SAATLRTYRD YLRNYTRDYS NYCINTYQTA FRGLNTRLHD\n" +
                    "     MLEFRTYMFL NVFEYVSIWS LFKYQSLMVS SGANLYASGS GPQQTQSFTA QNWPFLYSLF\n" +
                    "     QVNSNYILSG ISGTRLSITF PNIGGLPGST TTHSLNSARV NYSGGVSSGL IGATNLNHNF\n" +
                    "     NCSTVLPPLS TPFVRSWLDS GTDREGVATS TNWQTESFQT TLSLRCGAFS ARGNSNYFPD\n" +
                    "     YFIRNISGVP LVIRNEDLTR PLHYNQIRNI ESPSGTPGGA RAYLVSVHNR KNNIYAANEN\n" +
                    "     GTMIHLAPED YTGFTISPIH ATQVNNQTRT FISEKFGNQG DSLRFEQSNT TARYTLRGNG\n" +
                    "     NSYNLYLRVS SIGNSTIRVT INGRVYTVSN VNTTTNNDGV NDNGARFSDI NIGNIVASDN\n" +
                    "     TNVTLDINVT LNSGTPFDLM NIMFVPTNLP PLY\n" +
                    "//";

    private EntryObject entryObject;
    private UniprotLineParser<EntryObject> entryParser;

    private UniProtEntryMocker() {
        this.entryParser = new DefaultUniprotLineParserFactory().createEntryParser();
    }

    public static UniProtEntryMocker createDefaultEntry() {
        UniProtEntryMocker uniProtEntryMocker = new UniProtEntryMocker();
        uniProtEntryMocker.entryObject = uniProtEntryMocker.entryParser.parse(DEFAULT_ENTRY);

        return uniProtEntryMocker;
    }

    public static UniProtEntryMocker createEntryFromString(String entryText) {
        UniProtEntryMocker uniProtEntryMocker = new UniProtEntryMocker();
        uniProtEntryMocker.entryObject = uniProtEntryMocker.entryParser.parse(entryText);

        return uniProtEntryMocker;
    }

    public static UniProtEntryMocker createEntryFromInputStream(InputStream stream) throws IOException {
        StringBuilder entryText = new StringBuilder();

        try (InputStreamReader ir = new InputStreamReader(stream); BufferedReader br = new BufferedReader(ir)) {
            String line;

            while ((line = br.readLine()) != null) {
                entryText.append(line)
                        .append("\n");
            }
        }

        return createEntryFromString(entryText.toString());
    }

    public void updateEntryObject(LineType lineType, String replacement) {
        DefaultUniprotLineParserFactory parserFactory = new DefaultUniprotLineParserFactory();

        if (!replacement.endsWith("\n")) {
            replacement += "\n";
        }

        switch (lineType) {
            case AC:
                UniprotLineParser<AcLineObject> acLineParser = parserFactory.createAcLineParser();
                entryObject.ac = acLineParser.parse(replacement);
                break;
            case DE:
                UniprotLineParser<DeLineObject> deLineParser = parserFactory.createDeLineParser();
                entryObject.de = deLineParser.parse(replacement);
                break;
            case DR:
                UniprotLineParser<DrLineObject> drLineParser = parserFactory.createDrLineParser();
                entryObject.dr = drLineParser.parse(replacement);
                break;
            case DT:
                UniprotLineParser<DtLineObject> dtLineParser = parserFactory.createDtLineParser();
                entryObject.dt = dtLineParser.parse(replacement);
                break;
            case GN:
                UniprotLineParser<GnLineObject> gnLineParser = parserFactory.createGnLineParser();
                entryObject.gn = gnLineParser.parse(replacement);
                break;
            case ID:
                UniprotLineParser<IdLineObject> idLineParser = parserFactory.createIdLineParser();
                entryObject.id = idLineParser.parse(replacement);
                break;
            case KW:
                UniprotLineParser<KwLineObject> kwLineParser = parserFactory.createKwLineParser();
                entryObject.kw = kwLineParser.parse(replacement);
                break;
            case OC:
                UniprotLineParser<OcLineObject> ocLineParser = parserFactory.createOcLineParser();
                entryObject.oc = ocLineParser.parse(replacement);
                break;
            case OG:
                UniprotLineParser<OgLineObject> ogLineParser = parserFactory.createOgLineParser();
                entryObject.og = ogLineParser.parse(replacement);
                break;
            case OH:
                UniprotLineParser<OhLineObject> ohLineParser = parserFactory.createOhLineParser();
                entryObject.oh = ohLineParser.parse(replacement);
                break;
            case OS:
                UniprotLineParser<OsLineObject> osLineParser = parserFactory.createOsLineParser();
                entryObject.os = osLineParser.parse(replacement);
                break;
            case OX:
                UniprotLineParser<OxLineObject> oxLineParser = parserFactory.createOxLineParser();
                entryObject.ox = oxLineParser.parse(replacement);
                break;
            case CC:
                UniprotLineParser<CcLineObject> ccLineParser = parserFactory.createCcLineParser();
                entryObject.cc = ccLineParser.parse(replacement);
                break;
            case FT:
                UniprotLineParser<FtLineObject> ftLineParser = parserFactory.createFtLineParser();
                entryObject.ft = ftLineParser.parse(replacement);
                break;
            case PE:
                UniprotLineParser<PeLineObject> peLineParser = parserFactory.createPeLineParser();
                entryObject.pe = peLineParser.parse(replacement);
                break;
            default:
                throw new IllegalArgumentException("Line type to update not implemented: " + lineType);
        }
    }

    public UniProtEntry toUniProtEntry() {
        return new EntryObjectConverter(false).convert(this.entryObject);
    }
}
