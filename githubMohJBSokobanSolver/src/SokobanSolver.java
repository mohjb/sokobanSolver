//package soso;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class SokobanSolver extends JPanel
implements KeyListener,MouseListener,MouseMotionListener,ActionListener
{private static final long serialVersionUID = -8615408389885762774L;
Thread thread;

JFrame frame;
JTextArea ta;
static JLabel status;
static int ppr,ppc,mr,mc;


State state=new State(
"XXXXXXXXXXXXXXXXXXXXXX\n"+
"XOX  X               X\n"+
"X D    D     DG G    X\n"+
"X D  D D      G G    X\n"+
"X    D D      G G    X\n"+
"XXDDDD D  DGGGG G    X\n"+
"X      D        GG   X\n"+
"X  DDDDD   GGGGGGG   X\n"+
"X                    X\n"+
"X                    X\n"+
"XXXXXXXXXXXXXXXXXXXXXX\n"
		,true);
boolean saveUndo=true;
Cell design;

static void p(String p){System.out.println(p);}

static void alert(Object...a){JOptionPane.showMessageDialog(null, a[0]);}
static String prompt(Object...a){return JOptionPane.showInputDialog( a);}
static String status(String s){status.setText(s);return s;}
static String status(){return status.getText();}

JMenu menu;
String[]levels;

void loadLevels()
{try
 {	File f=new File("levels.levels");
	FileInputStream fr=new FileInputStream(f);
	byte[]b=new byte[(int)f.length()];
	fr.read(b);
	fr.close();
	String s=new String(b);
	p("loading levels.levels:\n"+s);
	int i=s.indexOf("\n\n"),n=s.length(),j=0;
	if(i==-1){levels=new String[1];levels[0]=s.trim();}else
	{	LinkedList<String>ll=new LinkedList<String>();
		while(j<n&&s.charAt(j)=='\n')j++;
		while(i!=-1)
		{	i+=2;
			ll.add(s.substring(j, i));j=i;
			while(j<n&&s.charAt(j)=='\n')j++;
			i=s.indexOf("\n\n",j);
		}if(j<n)ll.add(s.substring(j,n));
		levels=new String[ll.size()];
		ll.toArray(levels);
	}
 }catch(Exception x)
 {	x.printStackTrace();
	String[]ll={
"XXXX\nX  X\nXODGX\nXXXXX"

,
"XXXXXXXXXXXXXXXXXXXXXXX\n"+
"XOX  X               XX\n"+
"X D    D     DG G    XX\n"+
"X D  D D      G G    XX\n"+
"X    D D      G G    XX\n"+
"XXDDDD D  DGGGG G    XX\n"+
"X      D        GG   XX\n"+
"X  DDDDD   GGGGGGG   XX\n"+
"X                    XX\n"+
"X                    XX\n"+
"XXXXXXXXXXXXXXXXXXXXXXX\n"
,
"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n"+
"X                                     XX\n"+
"X     XXXXXXXXXX    XXXXXXXXXXXXXXX   XX\n"+
"X              X    X             X   XX\n"+
"X              X    X        O    X   XX\n"+
"X              XXXXXX             X   XX\n"+
"X                                 X   XX\n"+
"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

,



"XXXXX\n"+
"X   X\n"+
"X D X\n"+
"X OGX\n"+
"XXXXX"
,
"    XXXXX          \n"+
"    X   X          \n"+
"    XD  X          \n"+
"  XXX  DXX         \n"+
"  X  D D X         \n"+
"XXX X XX X   XXXXXX\n"+
"X   X XX XXXXX  GGX\n"+
"X D  D          GGX\n"+
"XXXXX XXX XPXX  GGX\n"+
"    X     XXXXXXXXX\n"+
"    XXXXXXX        "
,
"XXXXXXXXXXXX  \n"+
"XGG  X     XXX\n"+
"XGG  X D  D  X\n"+
"XGG  XDXXXX  X\n"+
"XGG    O XX  X\n"+
"XGG  X X  D XX\n"+
"XXXXXX XXD D X\n"+
"  X D  D D D X\n"+
"  X    X     X\n"+
"  XXXXXXXXXXXX"
,
"    XXXXX\n"+
"    X   X\n"+
"    XD  X\n"+
"  XXX  DXX\n"+
"  X  D D X\n"+
"XXX X XX X   XXXXXX\n"+
"X   X XX XXXXX  GGX\n"+
"X D  D          GGX\n"+
"XXXXX XXX XOXX  GGX\n"+
"    X     XXXXXXXXX\n"+
"    XXXXXXX        "
,
"XXXXX   \n"+
"X   XXXX\n"+
"X D X  X\n"+
"X   D OX\n"+
"XXXX  GX\n"+
"XX D XGX\n"+
"XX X XGX\n"+
"XX   XXX\n"+
"XXXXXXXX"
,

"XXXXXXXX\n"+
"XXXX_OXX\n"+
"XXXX___X\n"+
"XG_XDD_X\n"+
"X_____XX\n"+
"XG__DXXX\n"+
"XXG__XXX\n"+
"XXXXXXXX"
,
"  XXXXX \n"+
"XXX   X \n"+
"XGOD  X \n"+
"XXX DGX \n"+
"XGXXD X \n"+
"X X G XX\n"+
"XD dDDGX\n"+
"X   G  X\n"+
"XXXXXXXX"
,
"XXXXXXX\n"+
"X GGD X\n"+
"X X D X\n"+
"X X X X\n"+
"X DOX X\n"+
"XGD   X\n"+
"XGXXXXX\n"+
"XXX"
,
"  XXXX\n"+
"XXX  XXXX\n"+
"X     D X\n"+
"X X  XD X\n"+
"X G GXO X\n"+
"XXXXXXXXX"
,


"   XXXXX    \n"+
"XXXX   X    \n"+
"XGGXDD X    \n"+
"X GGD  XXXXX\n"+
"X OX X X   X\n"+
"X  D     D X\n"+
"X  XXXXXXXGX\n"+
"XXXX     XXX"

,
"   XXX\n"+
"  XX X XXXXX\n"+
" XX  XXX  XX\n"+
"XX D      XX\n"+
"X   OD X  XX\n"+
"XXX DXXX  XX\n"+
"  X  XGG  XX\n"+
" XX XXGX XXX\n"+
" X      XX\n"+
" X     XX\n"+
" XXXXXXX"
,
"   XXXXXXXXX\n"+
"   X      XX\n"+
"XXXX XXXX XX\n"+
"X D D GGX XX\n"+
"X X  GX X XX\n"+
"X X XXX X XX\n"+
"X XGX D   XX\n"+
"X XGDOD  XXX\n"+
"X XDXXX  X\n"+
"X  G     X\n"+
"XXXXXXXXXX"
,
"_______XXXXX\n"+
"_XXXXXXX___XXX\n"+
"XX_X_OXX_DD_XX\n"+
"X____D______XX\n"+
"X__D__XXX___XX\n"+
"XXX_XXXXXDXXXX\n"+
"X_D__XXX__GX\n"+
"X_D_D_D__GGX\n"+
"X____XXXGGGX\n"+
"X____X_XGGGX\n"+
"XXXXXX_XXXXX"
,
"XXXXXXXX\n"+
"X__X___X\n"+
"X___D__X\n"+
"X_DXX_XXXXXX\n"+
"XX_XG_XX__XX\n"+
"_X_XG___D_XX\n"+
"_X_XG_XX__XX\n"+
"_X_XGXXX_XXX\n"+
"_XO___XX__XX\n"+
"_XX_D_____XX\n"+
"__XXXXXX__XX\n"+
"_______XXXXX"

,
"_______XXXXXXXXX\n"+
"_XXXXXXX__X___XX\n"+
"_X_______DDGGGXX\n"+
"_X_XXXXXXDXGGGXX\n"+
"XX_X______XGGGXX\n"+
"X__X_XD_D_XXXXXX\n"+
"X_X_D_D_D_X____\n"+
"X_O__D_X__X____\n"+
"XXXXXD_DD_X____\n"+
"____X_____X____\n"+
"____XXXXXXX____"

,
"     XXXX\n"+
"     X  X\n"+
"     X  X\n"+
"     X  X\n"+
"     XD X\n"+
"     X  X\n"+
"     XD XX\n"+
"XXXXXX D XXX\n"+
"X G G   o XX\n"+
"XXXXXXXXXXXX"
,
"             XXXX\n"+
"             X XX\n"+
"XXXXXXXXXXXXXXoXX\n"+
"X            D XX\n"+
"X   D D D D D  XX\n"+
"XXXXXXXXXXXXX GXX\n"+
"            XG XX\n"+
"            X GXX\n"+
"            XG XX\n"+
"            X GXX\n"+
"            XX XX\n"+
"             XXXX"
,
"XXX\n"+
"X X\n"+
"XoXXXXXXXXXXXXXXX\n"+
"X D            XX\n"+
"X  D D D D D   XX\n"+
"XG XXXXXXXXXXXXXX\n"+
"X GX\n"+
"XG X\n"+
"X GX\n"+
"XG X\n"+
"X XX\n"+
"XXX"

,

"             XXXX\n"+
"             X XX\n"+
"XXXXXXXXXXXXXXoXX\n"+
"X            D XX\n"+
"X   D D D D D  XX\n"+
"XXXXXXXXXXXXX GXX\n"+
"            XG XX\n"+
"            X GXX\n"+
"            XG XX\n"+
"            X GXX\n"+
"            XX XX\n"+
"             XXXX"

,

"X\n"+
"XGXXX\n"+
"X O XXX\n"+
"X     XXX\n"+
"X  D    XXX\n"+
"XG   D D  X\n"+
"X    D   GX\n"+
"X  D D  XXX\n"+
"XG   GXXX\n"+
"X   XXX\n"+
"XGXXX\n"+
"XXX"
,
"XXXXX\n"+
"X O XXX\n"+
"XX XD  X\n"+
"X dG G X\n"+
"X  DD XX\n"+
"XXX XGX\n"+
"X   XX\n"+
"XXXXX"
,
"XXXXX\n"+
"X   XXXXX\n"+
"X X X   X\n"+
"X D   D X\n"+
"XGGXDXDXX\n"+
"XGOD   X\n"+
"XGG  XXX\n"+
"XXXXXX"
,
"XXXXXX          \n"+
"X   DG X  XXXXX \n"+
"XGD    X X    GX\n"+
"XXXX GXX XG  D X\n"+
"XXX  X  XXX  XX \n"+
"XX  X   XX DX   \n"+
"X  X    XG X    \n"+
"XXXXDXXXXXXXDXXX\n"+
"X       O      X\n"+
"XXXXXXXXXXXXXXXX"

,

"XXXXX\n"+
"XXX  OX\n"+
"X  D  X\n"+
"X    XXXXXXXXX\n"+
"X XD   GGGG  X\n"+
"X  D XXXXXXD X\n"+
"XX  XX    X  X\n"+
"XXXX     XXXX"
,
"XXXXX\n"+
"XX O X\n"+
"X    X  XXXXX    XXXXXX\n"+
"XX   XXXXX   XXXXXX    X\n"+
"XX   XX  GX   X   X  XX X\n"+
"X G XX DXGXX dD  XX XX  X\n"+
"X   XX  D  X GX  X     XX\n"+
"X   XXX    XX   XX DDXG X\n"+
"X  D  X  XXXX   XXXX  G X\n"+
"X GD  XX X  XX XX  XX XXX\n"+
"XXX XXXX X   X X    X X  \n"+
"X X  X X   X X    X X    \n"+
"XXX XXXX XXXXX XXXXXX XXX\n"+
"X                       X\n"+
"XXXXXXXXXXXXXXXXXXXXXXXXX"
,

"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n"+
"X                       G       XX\n"+
"X     XXXXX   XX   X  X   XX    XX\n"+
"X      D   X X  X  XX X  X  X   XX\n"+
"X      X   X XXXX  X XX  XXXX   XX\n"+
"X     XXXXX  X  X  X  X  X  X   XX\n"+
"X O                             XX\n"+
"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
,
"   XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n"+
"  XX             GX   XG                            GXX\n"+
"XXX   XXXXXXXXXXXXX X XX       X XXXXXXXXXXXXXXXXX   XX\n"+
"X     X             X GX       X                GX   XX\n"+
"X     X XXXXXXXX XXXXXXX       X                 X   XX\n"+
"X     X X  X   X X     X       X         O       X   XX\n"+
"X     X X  X X XXX X   X       X                 X   XX\n"+
"X     X      X     X   X      GX                 X   XX\n"+
"X     XXXXXXXXXXXXXX   X XXXXXXX                 X   XX\n"+
"X                                                    XX\n"+
"XXXXXXXXXXXXXXXXXXXXXXXXXX     XXXXXXXXXXXXXXXXXXXXXXXX\n"+
"XG                                                  GXX\n"+
"X     XXXXXXXXXXXXXXXXXX X       XXXXXXXXXXXXXXXXX   XX\n"+
"X     XG                 X       XG                  XX\n"+
"X     X  X               X       X   XXXX XXXX X     XX\n"+
"X     X  XGXXXX          X       X   X GX   GX X     XX\n"+
"X     X  XXG   XXXXXXX   X       X   X  XGX    X     XX\n"+
"X     X  X  XX  XG       XG      X   X    X XXXX  XXXXX\n"+
"X     X  X  XX  X        XXXXXXX X   X XXXX      GX\n"+
"XG       X                                       XX\n"+
"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
,
"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n"+
"O X                                        X       X      X      X               X                              XX\n"+
"X X XXXXXXXXXXXX  XXXXXXXXXXXXXXXXXXXXXXX  X XXXXX X  XXX X XXX  X XXXXXXXXXX X  X XXXXXX XXXXXXXXXXXXXXXXXXXXX XX\n"+
"X X X      X      X            X        X  X X   X X  X X X X X  X X    X   X X  X X    X X          X        X XX\n"+
"X X XXXXXX X XXXXXX X XXXXXXXX X XXXXXX X  X XXX X X  X X X X X  X X X  X X X X  X X X  X XXXXX  XXX XXXXXXXX X XX\n"+
"X X        X X    X X X      X X X    X X  X   X X X  X X X   X  X X X  X X X X  X X X  X     X  X            X XX\n"+
"X XXXXXXXXXX X X  X X X XXXX X X X X  X X  X X X X X  X X X XXX  X X X  X X XXX  X X XXXXXXXX X  XXXXXXXXXXXXXX XX\n"+
"X            X X  X X X X    X X X X  X X  X X X X X  X X X X    X X X  X X      X X      X X X                 XX\n"+
"XXXXXXXXXXXXXX XXXX X X X  XXX X X XXXX X  X X X X X  X X X X  X X X X  X XXXXX  X X X  X X X X  XXXXXXXXXXXXXXXXX\n"+
"X   X        X      X X X    X X X    X X  X X X X X  X X X X  X X X X  X     X  X X X  X X X X  X     X        XX\n"+
"X X X XXX  X XXXXXXXX X XXXX X X X X  X X  X X X X X  X X X X  X X XXX  XXXXX XXXX XXX  X X X X  X XXXXX  XXXXX XX\n"+
"X X X   X  X          X    X X X X X  X X  X X X X X  X X X X  X X X        X           X   X X  X            X XX\n"+
"X X XXXXX  XXXXXXXXXXXX X  X X X XXX  X X  XXX X X X  X X XXX  X X X XXXXXXXXXXXXXXXXXXXXXXXX X  XXXXXXXXXXXXXX XX\n"+
"X X     X         X     X  X X X      X X      X   X  X        X X X X                        X                 XX\n"+
"X XXXXX XXXXXXXX  XXXXXXX  X X XXXXXXXX XXXXXXXX XXX  XXXXXXXXXXXX X X  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX XX\n"+
"X X               X        X X        X        X X                 X X  X                          X X        X XX\n"+
"X X XXXXXXXXXXXXXXX XXXXXXXXXXXXXXXX  XXXXXXXX X X XXXXXXXXXXXXXXXXX X  XXXXX XXXXXXXXXXXXX XXXXXX X X XXXXXX X XX\n"+
"X X X   X                                      X X X        X        X      X X    X      X X    X X   X      X XX\n"+
"X XXX X XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX X X  XXX XXX  XXXXX XXXXXX X X  X X XXX  X X XXXX XXXXX  XXX X XX\n"+
"X   X X                                          X X    X          X      X X X  X X X X  X X             X X X XX\n"+
"X X X XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX X  XXXXXXXXXXXXXX  XXX X X X  X X X X  X XXXXXXXXXXXXXXX X X XX\n"+
"X X X X                 X    X     X             X X  X               X X X X X  X X   X  X            X    X X XX\n"+
"X X X X XXXXXXXXXXX XXX X  X X X X X  XXX  XXXXX X X  X XXXXXXXXXXXXXXX X X X X  X XXXXX  XXXXXXXXXXXX X  XXX X XX\n"+
"X X X X      X      X X X  X X X X X    X  X     X X  X X               X X X X  X          X        X X      X XX\n"+
"X X X XXXXXXXX XXXXXX X X  X XXX X XXXXXX  X XXXXX XXXX X XXX  X XXXXXXXX X X X  XXXXXXXXXX XXXXXXXX X X  XXXXX XX\n"+
"X X X X      X    X   X X  X     X         X     X X    X   X  X X        X X X      X    X          X X  X   X XX\n"+
"XXX X X  XXX XXX  X X X X  XXXXXXXXXXXXXXXXXXXXXXX X  XXXXXXX  X X XXXXXXXX X X  XXX X  XXXXXXXXXXXX X X  X XXX XX\n"+
"X   X X  X        X X X X                        X X  X        X X X    X   X X    X X             X X X  X   X XX\n"+
"X X X X  XXXXXXXXXX X X X  XXXXXXXXXXXXXXXXXXXXX X X  X XXXXXXXXXX XXXX X XXX XXXXXX XXXXXX XXX X  X X X  X X X XX\n"+
"X X X X    X      X X X X  X     X         X   X X X  X                 X X X X    X      X X X X  X X X  X X X XX\n"+
"X X X XXXX X XXX  X X X X  X XXX X X  XXX  X X X X X  XXXXXXXXXXXXXXXXXXX X X X  X XXXXX  X X X X  X X X  X X X XX\n"+
"X X X X    X X    X X X X    X X X X  X X  X X X X X                    X X X X  X        X X X X  X X X  X X X XX\n"+
"X X X X  XXX XXXXXX X X XXXXXX X XXX  X X  XXX X X XXXXXXXXXXXXX XXXXXX X X X X  XXXXXXXXXX X X XXXX X X  X X X XX\n"+
"X X X X  X          X X X      X      X        X               X X    X X X X X      X    X X X      X X  X X X XX\n"+
"X X X X  XXXXXXXXXXXX X X  XXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXXX  X X X  X X X X X  XXX X X  X X XXXXXXXX X  X X X XX\n"+
"X X X X  X            X X        X                 X    X   X  X X X  X X X X X  X   X X  X X        X X  X X X XX\n"+
"X XXX X  X XXXXXXXXXXXX XXXXXXXX X XXXXXXXXXXXXXXX X  XXX X X  X XXX  X X X X X  X XXXXX  X X X XXXX X X  XXX X XX\n"+
"X   X X  X                     X X X               X      X X  X      X X X   X  X X      X X X X    X X      X XX\n"+
"X X X X  XXXXXXXXXXXXXXXXXXXXX XXX X  XXXXXXXXXXXXXXXXXXXXX X  XXXXXXXX X XXX X  X X XXX  X X X XXXXXX XXXXXX X XX\n"+
"X X X X                      X X   X  X        X            X           X   X X  X X X X  X X X      X X      X XX\n"+
"X X X X  XXXXXXXXXXXXXXXXXXX X X XXX  X XXXXXX X X XXXXXXXXXXXXXXXXXXXX XXX X XXXX X X X  X X XXXXXX X X  XXXXX XX\n"+
"X X X X  X                 X X X X X  X X    X X X                    X X X X      X X    X X X      X X  X   X XX\n"+
"X X X X  X X XXXXXXXXXXXX  X X X X X  X XXX  X X XXXXXXXXXXXXXXX XXX  X X X XXXXXXXX XXXXXX XXX  XXX X X  X XXX XX\n"+
"X X X X  X X X      X      X X X X X  X X    X X X      X        X    X X X      X               X X X X  X   X XX\n"+
"XXX X X  X X X XXX  X XXXXXX X X X X  X X X  X X X XXX  X XXX  XXXXXXXX X XXXXX  XXXXXXXXXXXXXXXXX X X X  X X X XX\n"+
"X   X X  X X X X    X X X    X X X X  X X X  X X X X X  X X X  X              X             X      X X X  X X X XX\n"+
"X X X X  XXX X XXXXXX X X  XXX X X X  X X XXXX X X X X  X X X  X XXXXXXXXXXXXXXXXXXXXXXXXXX X XXXXXX X X  X X X XX\n"+
"X X X X      X          X  X X X X X  X X      X X X X  X X X  X X                          X        X X  X X X XX\n"+
"X X X XXXXXXXXXXXXXXXXX X  X X X X X  X XXXXXX X X X X  X X X  X X XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX X  XXX X XX\n"+
"X X X                 X X  X X X X    X      X X X X X  X X    X X X                                   X      X XX\n"+
"X X XXXXXXXXXXXXXXXX  XXX  X X X XXX  X XXX  XXX X X X  X XXXXXX X X  XXXXXXXXXXXX XXXXXXXXXXXXXX  XXXXXXXXXXXX XX\n"+
"X X X              X  X    X   X   X  X X X  X   X X    X        X X  X        X X X               X            XX\n"+
"X X XXXXXXXX XXXXXXX  X XXXX XXXXX X  X X X  X XXX X XXXXXXXXXXXXX X  X XXXXX  X X X XXXXXXXXXXXXXXX XXXXXXXXXXXXX\n"+
"X X          X        X X    X     X  X X X  X X   X             X X  X X   X  X X X        X      X X          XX\n"+
"X XXXXXXXXXX X XXXXXXXX X  XXXXX XXX  X X X  X X X XXXXXXXXXXXXXXX X  X X XXX  X X XXXXXXXX X X X  X XXXXXXXXXX XX\n"+
"X X        X X X        X  X   X X    X X X  X X X X               X    X   X  X X X      X X X X  X            XX\n"+
"X X XXXXXXXX X X  X XXXXX  X X X X XXXX X X  X XXX X XXXXXXXXXXXXXXXXXXXXXX X  X X X XXXXXX XXX XXXXXXXXXXXXXXX XX\n"+
"X X          X X  X X   X  X X X X X      X        X X  D  G   X            X  X X X            X               XX\n"+
"X XXXXXXXXXXXX XXXX X XXX  X X X X XXXXXXXXXXXXXXXXX X  XXXXXXXX XXX  XXXXXXX  X X XXXXXXXXXXXXXX  XXXXXXXXXXXXXXX\n"+
"X                   X        X   X                   XD          DGX             X                              XX\n"+
"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXGXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n"
};levels=ll;
}if(levels!=null)
for(int i=0;i<levels.length;i++)menu("level "+i);
}

void menu(String s){
	JMenuItem mi=new JMenuItem(s);
	menu.add(mi);mi.addActionListener(this);}

 public SokobanSolver() {
	frame=new JFrame("SokobanSolver 2013, by mohamadjb@gmail.com");
	Container cp=frame.getContentPane();
	cp.setLayout(new BorderLayout());
	cp.add(ta=new JTextArea(),BorderLayout.NORTH);
	ta.setFont(new Font(Font.MONOSPACED,Font.PLAIN,24));
	cp.add(this,BorderLayout.CENTER);
	cp.add(status=new JLabel(),BorderLayout.SOUTH);
	ta.addKeyListener(this);
	addKeyListener(this);
	addMouseListener(this);
	addMouseMotionListener(this);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setBounds(5,5,640,480);
	ta.setText(state.toString());
	JMenuBar mb=new JMenuBar();
	frame.setJMenuBar(mb);
	menu=new JMenu("Menu");
	mb.add(menu);
	menu("load class");
	menu("javascript");
	menu("load from text area(keyboard:alt+enter)");
	menu("save to text area(keyboard:alt+space)");
	menu("Set Width");
	menu("Set Height");
	menu("put with mouse-click:Goal(keyboard:G)");
	menu("put with mouse-click:Wall(keyboard:X)");
	menu("put with mouse-click:Box(keyboard:D)");
	menu("put with mouse-click:Space(keyboard:space)");
	menu("put with mouse-click:Player(keyboard:P)");
	menu("mouse-click move(keyboard:M)");//menu("mousePath toggle");
	loadLevels();
	frame.setVisible(true);status("loaded");}

 //static Map<String,BufferedImage>imgs=new HashMap<String,BufferedImage>();

// static BufferedImage img(Cell c){BufferedImage r=imgs.get(c);if(r==null)try{imgs.put(c, r=ImageIO.read(new File(c.fileName)));}catch(Exception x){x.printStackTrace();}return r;}
 
// static BufferedImage imgOld(String fileName){BufferedImage r=imgs.get(fileName);if(r==null)try{imgs.put(fileName, r=ImageIO.read(new File(fileName)));}catch(Exception x){x.printStackTrace();}return r;}

 public static void main(String[]args){new SokobanSolver();}

enum Cell{/**Wall obstacle*/X ,/**space*/_(true)
	,/**Crater or block or box that can be pushed*/D
	,/**Goal location*/G(true) ,/**player location*/O(true)
	,/**player standing on a goal*/o(true)
	,/**a crater or block or box on a goal*/d;
//BufferedImage img(){return SokobanSolver.img((this==o?"og":this==d?"dg":toString())+".png");}
//static Map<Cell,BufferedImage>imgs=new HashMap<Cell,BufferedImage>();
BufferedImage img;BufferedImage img(){return img;}
//File imgFile(){return new File();}

 public boolean free;Cell(){this(false);}
 Cell(boolean f){free=f;loadImage();}

 void loadImage()
 {Cell t=this;try
 {String fileName=(t==o?"og":(t==d?"dg":toString()))+".png";
	img=ImageIO.read(new File(fileName));}
 catch(Exception x)
 {	x.printStackTrace();
	img=new BufferedImage(16,16,BufferedImage.TYPE_INT_RGB);
	Graphics g=img.getGraphics();String s=toString();char c=s.charAt(0);
	g.setColor(c=='X'?java.awt.Color.black
		:c=='D'?java.awt.Color.yellow
		:c=='o'||c=='d'||c=='G'?java.awt.Color.green
		:java.awt.Color.white);
	g.fillRect(0,0,16,16);
	g.setColor(c=='O'||c=='o'?java.awt.Color.red:java.awt.Color.blue);
	g.drawString(s, 0, 10);}}

}//enum Cell

enum Direction{up(true,false,0,-1),dn(true,true,0,1),rt(false,true,1,0),lf(false,false,-1,0);
 public boolean vert,postv;public int dx,dy;
 Direction(boolean v,boolean p,int x,int y){vert=v;postv=p;dx=x;dy=y;}
 static Direction dir(int x1,int y1,int x2,int y2)
 {int dx=x2-x1,dy=y2-y1;
 	return Math.abs(dx)>Math.abs(dy)
 		?(dx<0?lf:rt):(dy<0?up:dn);}
 //static Direction dir(int a,int b,int c){return dir(a%c,a/c , b%c,b/c);}
 static Direction dir(DirNode a,DirNode b){return dir(a.c,a.r , b.c,b.r);}
 //int loc(int loc,int c){int y=loc/c,x=loc%c;return (x+dx)+(y+dy)*c;} 
 Direction opposite(){return this==dn?up:this==up?dn:this==lf?rt:lf;}
}//enum Direction

class DirNode
{/**the move from the previous state to this state*/
Direction dir;DirNode p,n;
/**pathLength is the steps count from the start of the path to this node.
the estimated is the steps count from the start to the target location using the manhattenDistance
*/public int estimated=Integer.MAX_VALUE,pathLength=Integer.MAX_VALUE,r,c;//,location;

 public String locStr(){return "r:"+r+" ,c:"+c;}
@Override public String toString()
{return "{dir:"+dir+" ,"+locStr()
	+" ,pathLength:"+pathLength+" ,estimated:"+estimated
	+" ,p:"+(p==null?"null":"{"+p.locStr()+"}")
	+" ,n:"+(n==null?"null":"{"+n.locStr()+"}")+"}";}

DirNode(int r,int c){init(r,c);}//location=loc;
//DirNode(Direction d,DirNode p){dir=d;if((this.p=p)!=null)p.n=this;}
//DirNode(DirNode p,int r,int c){loc(r,c);if((this.p=p)!=null)p.n=this;}
DirNode init(int r,int c){loc(r,c);n=p=null;dir=null;estimated=pathLength=Integer.MAX_VALUE;return this;}
void loc(int row,int col){r=row;c=col;}//location=loc;
//DirNode copy(){return copy(null);}DirNode copy(DirNode n){DirNode r=new DirNode(dir,null);r.p=p==null?p:p.copy(r);r.n=n;return r;}

/**returns next, unlinks this.*/
DirNode dequeue(){if(n!=null)n.p=p;if(p!=null)p.n=n;DirNode r=n;n=p=null;return r;}

/**links x after this ;returns x*/
DirNode link(DirNode x){if(x!=null){if(x.p!=null||x.n!=null)x.dequeue();x.p=this;x.n=n;if(n!=null)n.p=x;n=x;}return x;}

/**links x Before this ;returns x*/
DirNode linkBefore(DirNode x){if(x!=null){if(x.p!=null||x.n!=null)x.dequeue();x.n=this;x.p=p;if(p!=null)p.n=x;p=x;}return x;}

/**find estimate node*/
DirNode findE(int e,DirNode p)
{DirNode n=this;
 while(e>n.n.estimated && e<p.estimated){n=n.n;p=p.p;}
 return e>=p.estimated?p:n;}

/*grid distance*/int dist(DirNode b){return Math.abs(r-b.r)+Math.abs(c-b.c);}

/**find Neighbor that has the smallest pathLength, neighbors on the grid* /
DirNode findNbr(PathFinder p)
{DirNode n=null,x=null;
	for(Direction d:Direction.values())
	{	x=p.grid(r+d.dy, c+d.dx);
		if(n==null||n.pathLength>x.pathLength)
			n=x;
	}
	//dir=n==null?null:Direction.dir(this, n);
	return n;
}*/

}//class DirNode

class State
{Cell a[ ][ ];
 int Cols,/**count of craters on goals*/countd
 ,/**count of goals which are not filled*/countG
 ,/**count of Craters which are not on goal*/countD;

 DirNode dirnode;

 /**nxt:if true s.next=this state and this.prev=s, otherwise this.next=s and s.prev=this*/
 public State(State p){copy(p);}

 public State(String p,boolean playerPosition){load(p,playerPosition);}

void load(String p,boolean playerPosition)
{String[]lines=p.split("\n");Cols=0;
 int ln=lines.length,li=0;if(mousePathFinder!=null)mousePathFinder.grid=null;
 a=new Cell[ln][];countd=countD=countG=0;
 for(;li<ln;li++)
 {	String line=lines[li];
	int n=line.length(),i=0;
	a[li]=new Cell[n];
	for(;i<n;i++)try{if(Cols<i)Cols=i;
		char c=line.charAt(i);
		if(c==' ')c='_';else 
		if(playerPosition&&(c=='O'||c=='o'||c=='P'||c=='p'))
		{ppc=i;ppr=li;}
		a[li][i]=Cell.valueOf(String.valueOf(c));
		if(a[li][i]==Cell.D)countD++;
		if(a[li][i]==Cell.G||a[li][i]==Cell.o)countG++;
		if(a[li][i]==Cell.d)countd++;
		p("a[row:"+li+"][col:"+i+"]="+a[li][i]);
}catch(Exception x){x.printStackTrace();}}}

@Override
public String toString()
{	StringBuilder sb=new StringBuilder();
	int rows=a.length,cols=getWidth(),row,col;
	for(row=0;row<rows;row++)try{
		for(col=0;col<cols;col++)try{
			Cell c=getCell(row, col);
			sb.append(c==null?"N":c.toString());
		}catch(Exception x){p("row="+row+" ,col="+col+" ,Exception:");x.printStackTrace();}
		sb.append("\n");
	}catch(Exception x){p("row="+row+" ,Exception:");x.printStackTrace();}
	return sb.toString();}

 int getWidth(){return getWidth(false);}

 int getWidth(Boolean update)
 {if(update){Cols=0;for(int i=0,n=a.length;i<n;i++)if(Cols<a[i].length)Cols=a[i].length;
	}return Cols;}


 //public State copy(){if(next==null)next=new State(this);else next.copy(this);return next;}
 public State copy(){return new State(this);}

 /**return a copy ,which will be the this.next modified from this state to a move by direction parameter*/
public State copy(Direction p){copy();dirnode.dir=p;
	int r=ppr,c=ppc;
	mov(ppr, ppc, p, false);//p==p.up||p==p.dn, p==p.rt||p==p.dn
	ppr=r;ppc=c;return this;}

public State copy(State p)
{if(a==null||a.length!=p.a.length)a=new Cell[p.a.length][];Cols=p.Cols;
	for(int row=0;row<a.length;row++)
	{	if(a[row]==null||a[row].length!=p.a[row].length)
			a[row]=new Cell[p.a[row].length];
		for(int col=0;col<a[row].length;col++)
			a[row][col]=p.a[row][col];
	}dirnode=p.dirnode;return this;}

/**returns a count which is the number of craters that are on not on any goal.*/
int updateCounts()
{countD=0;countd=0;countG=0;for(int row=0;row<a.length;row++)
 for(int col=0;col<a[row].length;col++)
 {Cell v=a[row][col];if(v==Cell.D)countD++;else if(v==Cell.d)countd++;else if(v==Cell.G)countG++;
 }return countD;}

//Cell getCell(int location){return getCell(location/cols,location%cols);}

//Cell getCell(int location,Direction dir){return getCell(dir.dy+location/cols,dir.dx+location%cols);}

Cell getCell(int row,int col)
{return row<0||col<0
	||a.length<=row
	||a[row].length<=col
	?null
	:a[row][col];}

//Cell getCell(int row,int col,boolean dimY,boolean dirPstv){if(dimY)row+=dirPstv?1:-1;else col+=dirPstv?1:-1;return getCell(row, col);}
//Cell getCell(int row,int col,Direction dir){return getCell(dir.dy+row, dir.dx+col);}

boolean isCanMove(int row,int col,Direction dir)
{	Cell v=getCell(row+dir.dy, col+dir.dx);
	if(v==null||v==Cell.X)return false;
	if(dir.vert)row+=dir.dy;else col+=dir.dx;
	if(v==Cell.D||v==Cell.d){Cell v2=getCell(row+dir.dy, col+dir.dx);
		return v2!=null&&v2.free;}//!=Cell.X&&v2!=Cell.D&&v2!=Cell.d;
 return true;
}

/*boolean isCanMove(int row,int col,boolean dimY,boolean dirPstv)
{	Cell v=getCell(row, col, dimY, dirPstv);
	if(v==null||v==Cell.X)return false;
	if(dimY)row+=dirPstv?1:-1;else col+=dirPstv?1:-1;
	if(v==Cell.D||v==Cell.d){Cell v2=getCell(row, col, dimY, dirPstv);
		return v2!=null&&v2!=Cell.X&&v2!=Cell.D&&v2!=Cell.d;}
	return true;}*/

boolean mov(int row,int col,Direction dir,boolean updatePlayerPosition)//,boolean dimY,boolean dirPstv
{p("state.mov(row:"+row+" , col:"+col+" , dir:"+dir+")");
 if(!isCanMove(row, col, dir))return false;
 if(saveUndo);//{State old=prev,nw=copy();nw.prev=old;old.next=nw;prev=nw;nw.next=this;}
 int r1=row,c1=col;
 if(dir.vert)r1+=dir.dy;else c1+=dir.dx;
 if(updatePlayerPosition){ppr=r1;ppc=c1;}
 Cell v0=getCell(row, col),v1=getCell(r1 , c1);
 a[row][col]=v0==Cell.o?Cell.G:Cell._;
 a[r1][c1]=v1==Cell.G||v1==Cell.d?Cell.o:Cell.O;
 if(v1==Cell.D||v1==Cell.d)
 {	p("state.move:crater:row1="+r1+" ,col1="+c1+" ,v1="+v1);
	int r2=r1,c2=c1;
	if(dir.vert)r2+=dir.dy;else c2+=dir.dx;
	Cell v2=getCell(r2,c2);
	a[r2][c2]=v2==Cell.G?Cell.d:Cell.D;
	if(v2==Cell.G&&v1!=Cell.d){countd++;countG--;countD--;}
	else if(v1==Cell.d&&v2!=Cell.G){countd--;countG++;countD++;}//updateCounts();
 }return true;}

/**returns ,row*Cols+col, the location of the player/Cell.O in this grid
 * , row is the row-location-of-the-player 
 * , col is the col-location-of-the-player
 * , Cols is this.getWidth() */
 int pLoc()
 {for(int row=0;row<a.length;row++)
	for(int col=0;col<a[row].length;col++)
	if(a[row][col]==Cell.o||a[row][col]==Cell.O)
		return Cols*row+col;
	return -1;}

/**move the player-location
 * actually changing the cell at the old-location(pr,pc) to space
 * 	and changing the cell at the new-location(nr,nc) to Cell.o or Cell.O
 * */
void pLoc(int pr,int pc,int nr,int nc)
{	Cell v=getCell(pr,pc);if(v!=null)a[pr][pc]=v==Cell.o?Cell.G:Cell._;
	v=getCell(nr,nc);if(v!=null)a[nr][nc]=v==Cell.G?Cell.o:Cell.O;}

/**move the player-location
 * actually changing the cell at the old-location(pr,pc) to space
 * 	and changing the cell at the new-location(nr,nc) to Cell.o or Cell.O
 * */void pLoc(int nr,int nc)
{	int pr=pLoc(),pc=pr%Cols;pr=pr/Cols;Cell 
	v=getCell(pr,pc);if(v!=null)a[pr][pc]=v==Cell.o?Cell.G:Cell._;
	v=getCell(nr,nc);if(v!=null)a[nr][nc]=v==Cell.G?Cell.o:Cell.O;}

 void bLoc(int pr,int pc,int nr,int nc)
 {	Cell v=getCell(pr,pc);if(v!=null)a[pr][pc]=v==Cell.d?Cell.G:Cell._;
	v=getCell(nr,nc);if(v!=null)a[nr][nc]=v==Cell.G?Cell.d:Cell.D;}

}//class State

class PathFinder
{//PathFinder(State p){s=p;}
	DirNode q,start,target,grid[][];State s;

 int size(){int r=0;DirNode p=target;while(p!=null){r++;p=p.p;}return r;}

 DirNode len(DirNode m,DirNode n2)
 {	if(m==null)return m;
	m.pathLength=n2==null?0:n2.pathLength+m.dist(n2);
	m.estimated=m.pathLength+m.dist(target);return m;}

 DirNode grid(int y,int x)
 {return y>=0&& y<grid.length 
	&&grid[y]!=null&&x>=0&& 
	x<grid[y].length?grid[y][x]:null;}

 void findPath(State p,int startRow,int startCol,int targetRow,int targetCol)
 {s=state;findPath(startRow,startCol,targetRow,targetCol);}

 void findPath(int startRow,int startCol,int targetRow,int targetCol)
 {	DirNode d=start;start=null;
	if(startCol==targetCol && startRow==targetRow)return;
	Cell c=s.getCell(targetRow,targetCol);
	if(c==null || !c.free)return;

	if(d==null)d=new DirNode(startRow,startCol);
	else d.init(startRow,startCol);
	d.dir=Direction.dir(startCol, startRow, targetCol, targetRow);
	q=start=d;

	d=target;
	if(d==null)d=new DirNode(targetRow,targetCol);
	else d.init(targetRow,targetCol);
	target=d;
	start.pathLength=0;
	start.estimated=target.estimated=start.dist(target);
	if(start.estimated==1){start.link(target);return;}

	//gridClear:
	if(grid==null||grid.length!=s.a.length)
		grid=new DirNode[s.a.length][];
	for(int i=0;i<grid.length;i++)
	{	if(grid[i]==null||grid[i].length!=s.a[i].length)
			grid[i]=new DirNode[s.a[i].length];else 
		for(int j=0;j<grid[i].length;j++)grid[i][j]=null;
	}

	if(target.r<0||target.r>=grid.length||grid[target.r]==null||target.c<0||target.c>=grid[target.r].length)
		{p(" ArrayIndexOutOfBounds:target:"+target);return;}
	else grid[target.r][target.c]=target;


	if(start.r<0||start.r>=grid.length||grid[start.r]==null||start.c<0||start.c>=grid[start.r].length)
		{p(" ArrayIndexOutOfBounds:start:"+start);return;}
	else grid[start .r][start .c]=start;

	findPath();
	int z=grid.length*cols;
	d=target;start.p=target.n=null;
	while(d!=null && d!=start && --z>0)
	{	DirNode m=grid(d.r+d.dir.dy,d.c+d.dir.dx);//d.p;
		for(Direction dir:Direction.values())
		{	DirNode nbr=grid(d.r+dir.dy,d.c+dir.dx);
			if(nbr!=null&&(m==null||nbr.pathLength<m.pathLength))
				m=nbr;
		}m.n=d;d.p=m;
		m.dir=Direction.dir(m, d);
		d=m;
	}grid=null;target.dir=null;
 }
 
 void findPath()
 {	DirNode qTail=q=start,nbr,n2;
	while(q!=null)
	{DirNode m=q;q=q.dequeue();
		for(Direction dir:Direction.values())
		{	int x=m.c+dir.dx,y=m.r+dir.dy;
			Cell c=s.getCell(y,x);
			if(c!=null&&c.free)
			{	if((nbr=grid(y,x))==null)
				{	nbr=grid[y][x]=new DirNode(y,x);n2=m;
					n2=findNbr(nbr);if(n2==null)n2=m;//find the shortest step in terms of pathLength
					nbr.dir=Direction.dir(nbr, n2);//the way back
					len(nbr,n2);//nbr.pathLength=n2.pathLength+nbr.dist(n2);//nbr.dist(start);
					//nbr.estimated=nbr.pathLength+nbr.dist(target);
					if(q==null)q=qTail=nbr;else //enqueue the nbr
					if(nbr.estimated<q.estimated)
						q=q.linkBefore(nbr);else 
					if(nbr.estimated>=qTail.estimated)
						qTail=qTail.link(nbr);
					else
						q.findE(nbr.estimated,qTail).link(nbr);
				}else if(nbr==target)
				{	target.dir=dir.opposite();//m.link(target);
					return;
				}
			}
			//need first get nod(m.x,y,m.dir) , then, compare pathLength,which is, findNbr(m).link(m);m.pathLength=m.p.pathLength+m.dist(m.p);
		}
	}
 }//findPath

 /**find Neighbor that has the smallest pathLength, neighbors on the grid*/
 DirNode findNbr(DirNode t)
 {DirNode n=null,x=null;
 	for(Direction d:Direction.values())
 	{	x=grid(t.r+d.dy, t.c+d.dx);
 		if(n==null||(x!=null&&n.
 			pathLength>x.pathLength))
 			n=x;
 	}
 	//t.dir=n==null?null:Direction.dir(t, n);
 	return n;
 }

}//class PathFinder 

class BlockMoves extends State
{BlockMoves(State p){super(p);}
 PathFinder pf;
/**blockMoves[i]=r*Cols+c */
List<Integer>possibleLocations;//blockMoves
boolean canMove(int r,int c){return possibleLocations.contains(r*Cols+c);}

void find(State p,int row,int col,int cr,int cc)
{if(possibleLocations==null)possibleLocations=new LinkedList<Integer>();
 else possibleLocations.clear();copy(p);
 recurs(row,col,cr,cc,getWidth());
}//findBlockMoves

boolean canReach(int pr,int pc,int tr,int tc)
{if(pf==null)pf=new PathFinder();//this);
	//pf.pLoc(pr,pc);//pf.gridClear();
	pf.findPath(this, pr, pc, tr,tc);
	return pf.start!=null;}

 void recurs(int row,int col,int pr,int pc,int Cols)
 {Cell v=getCell(row-1, col)
	,p=getCell(row+1, col);
	int i=(row-1)*Cols+col;
	//up
	if(v!=null&&v!=Cell.X&&v!=Cell.D&&v!=Cell.d
	 &&p!=null&&p!=Cell.X&&p!=Cell.D&&p!=Cell.d
	 &&!possibleLocations.contains(i)
	 &&canReach(pr,pc,row-1,col))
	{possibleLocations.add(i);recurs(row-1, col, row, col, Cols);}

	//dn
	//v=getCell(row+1, col);
	//p=getCell(row-1, col);
	i=(row-1)*Cols+col;
	if(v!=null&&v!=Cell.X&&v!=Cell.D&&v!=Cell.d
		&&p!=null&&p!=Cell.X&&p!=Cell.D&&p!=Cell.d
		&&!possibleLocations.contains(i)
		&&canReach(pr,pc,row+1,col))
	{possibleLocations.add(i);recurs(row+1, col, row, col, Cols);}

	//rt
	v=getCell(row, col+1);
	p=getCell(row, col-1);
	i=row*Cols+col+1;
	if(v!=null&&v!=Cell.X&&v!=Cell.D&&v!=Cell.d
		&&p!=null&&p!=Cell.X&&p!=Cell.D&&p!=Cell.d
		&&!possibleLocations.contains(i)
		&&canReach(pr,pc,row,col+1))
	{possibleLocations.add(i);recurs(row, col, row, col+1, Cols);}

	//lf
	//v=getCell(row, col+1);
	//p=getCell(row, col-1);
	i=row*Cols+col-1;
	if(v!=null&&v!=Cell.X&&v!=Cell.D&&v!=Cell.d
		&&p!=null&&p!=Cell.X&&p!=Cell.D&&p!=Cell.d
		&&!possibleLocations.contains(i)
		&&canReach(pr,pc,row,col-1))
	{possibleLocations.add(i);recurs(row, col, row, col-1, Cols);}

 }//findBlockMoves

}//class BlockMoves

static int w,w2,h,h2,rows,cols;
static void line(Graphics g,int r1,int c1,int r2,int c2)
{g.drawLine(c1*w+w2, r1*h+h2, c2*w+w2, r2*h+h2);}

@Override
public void paint(Graphics g){
	super.paint(g);Dimension dim=getSize();
	rows=state.a.length;cols=state.getWidth();
	w=dim.width/cols;w2=w/2;
	h=dim.height/rows;h2=h/2;
	for(int row=0;row<rows;row++)try
	{int y=row*h;
		//g.drawLine(0,y, dim.width, y);
		for(int col=0;col<=cols;col++)try
		{int x=col*w;
			//g.drawLine(x, 0, x, dim.height);
			Cell c=state.getCell(row, col);
			//g.drawString((c!=null)?c.toString():"null", x+w/2, y+h/2);
			g.drawImage(((c!=null)?c:Cell._).img(), x, y, w, h, null);
		}catch(Exception x){p("paint:row="+row+" ,col="+col+" ,Exception:");x.printStackTrace();}
	}catch(Exception x){p("paint:row="+row+" ,Exception:");x.printStackTrace();}
	/*/g.drawLine(ppc*w+w/2, ppr*h+h/2, mc*w+w/2, mr*h+h/2);
	int r0=ppr,c0=ppc,r1=r0,c1=c0;
	/*if(mousePathMode==MousePathMode.auto)
	{
	for(int i=0;i<mousePath.size();i++)
	{	Direction d=mousePath.get(i);
		if(d==Direction.up)r1--;else
		if(d==Direction.dn)r1++;else
		if(d==Direction.rt)c1++;else
		if(d==Direction.lf)c1--;
		g.drawLine(c0*w+w2, r0*h+h2, c1*w+w2, r1*h+h2);
		c0=c1;r0=r1;
	}
	if(mousePath.size()>0)g.drawLine(c0*w+w2, r0*h+h2, mc*w+w2, mr*h+h2);
	}*/int mpc=pathAnim.paint(g);
	g.drawString("done:"+state.countd+"/"
	+(state.countG+state.countd)
	+(state.countG==0?" , you Won":" ,remaining:"
	+state.countD)+(mpc!=0?",mpc="+mpc:"")
	, ppc*w, ppr*h);
}//paint

public void mouseMoved(MouseEvent e)
{	if(thread!=null)return;
	Dimension dim=getSize();
	rows=state.a.length;cols=state.getWidth();
	w=dim.width/cols;h=dim.height/rows;
	int r=e.getY()/h,c=e.getX()/w;
	if(r!=mr||c!=mc)
	{	mr=r;mc=c;
		//if(e.getClickCount()>0)
		updateMousePath();
		repaint();}}


void updateMousePath()
{	if(mousePathFinder==null)
		mousePathFinder=new PathFinder();//state);
	mousePathFinder.findPath(state, ppr, ppc, mr, mc);}

PathFinder mousePathFinder;
BlockMoves blockMoves;


public void mouseClicked(MouseEvent e) 
{if(thread!=null){thread=null;mouseMoved(e);return;}
 if(blockMoves!=null&&blockMoves.canMove(mr, mc)){}
 updateMousePath();
 if(mousePathFinder.start!=null)
	{pathAnim.startMousePath();
	 repaint();}
 else{Cell v=state.getCell(mr,mc);
	if(v==Cell.d||v==Cell.D)
	{if(blockMoves==null)blockMoves=new BlockMoves(state);
	 blockMoves.find(state, mr, mc, ppr, ppc);
	}
} }


public void mouseEntered(MouseEvent e) {}

public void mouseExited(MouseEvent e) {}

public void mousePressed(MouseEvent e) {}

public void mouseReleased(MouseEvent e) {}

public void mouseDragged(MouseEvent arg0) {}

public void keyPressed(KeyEvent e){}

public void keyReleased(KeyEvent e) {keyTyped(e);}

public void keyTyped(KeyEvent e)
{int kc=e.getKeyCode();
	p("keyTyped:isAltDown="+e.isAltDown()+" ,getKeyCode="+kc+" ,event="+e);
	if(e.isAltDown()&&kc==KeyEvent.VK_SPACE)
		ta.setText(state.toString());
	boolean b=false;
	if(b=(e.isAltDown()&&kc==KeyEvent.VK_ENTER))
		state.load(ta.getText(),true);else
	if(b=kc==KeyEvent.VK_UP  )state.mov(ppr,ppc,Direction.up,true);
	else if(b=kc==KeyEvent.VK_DOWN)state.mov(ppr,ppc,Direction.dn ,true);
	else if(b=kc==KeyEvent.VK_LEFT)state.mov(ppr,ppc,Direction.lf,true);
	else if(b=kc==KeyEvent.VK_RIGHT)state.mov(ppr,ppc,Direction.rt,true);
	//else if(b=kc==KeyEvent.VK_Z)state=state.prev;
	//if(kc==KeyEvent.VK_F1){if(base!=null)base=null;else{base=top=new MousePathStackFrame(state.copy(), ppr, ppc);if(!b)updateMousePath();}}
	//if(kc==KeyEvent.VK_F2&&top!=null)top.computeMP();
	if(kc==KeyEvent.VK_G)design=Cell.G;
	if(kc==KeyEvent.VK_X)design=Cell.X;
	if(kc==KeyEvent.VK_D)design=Cell.D;
	if(kc==KeyEvent.VK_SPACE)design=Cell._;
	if(kc==KeyEvent.VK_P)design=Cell.O;
	if(kc==KeyEvent.VK_M)design=null;
	if(b){if(design==null)updateMousePath();repaint();}}

public void actionPerformed(ActionEvent e)
{//Object src=e.getSource();
	String s=e.getActionCommand();
	if("load class".equals(s)){s=prompt("type class name");if(s!=null)try{p(String.
		valueOf(Class.forName(s)));}catch(Exception x){x.printStackTrace();}}
	else if(s.startsWith("level "))
	{int i=Integer.parseInt(s.substring(6));
	 ta.setText(levels[i]);p("\nload:\n"+levels[i]);
	 state.load(levels[i], true);status.setText(s);}
	else if(s.startsWith("load")){}
	else if(s.startsWith("save")){}
	else if("Set Width".equals(s)){}
	else if("Set Height".equals(s)){}
	else if("put with mouse-click:Goal(keyboard:G)".equals(s)){}
	else if("put with mouse-click:Wall(keyboard:X)".equals(s)){}
	else if("put with mouse-click:Box(keyboard:D)".equals(s)){}
	else if("put with mouse-click:Space(keyboard:space)".equals(s)){}
	else if("put with mouse-click:Player(keyboard:P)".equals(s)){}
	else if("mouse-click move(keyboard:M)".equals(s)){}
}
PathAnim pathAnim=new PathAnim();
class PathAnim implements Runnable{

void startMousePath(){q=mousePathFinder.start;startThread();}//mousePathTrgt[0]=mr;mousePathTrgt[1]=mc;
//void startBlockMove(){}
DirNode q;
int mousePathOffset[]={0,0}//x,y
//,mousePathTrgt[]={-1,-1}//r,c
,mousePathOffsetSpeed=5;
int paint(Graphics g)
{	int mpc=0;
	try{DirNode dn=q;//mousePathFinder==null?null:mousePathFinder.start;
		int r0=ppr,c0=ppc,r1=r0,c1=c0;
		while(dn!=null){mpc++;Direction d=dn.dir;
		if(d.vert)r1+=d.dy;else c1+=d.dx;
		g.drawLine(c0*w+w2, r0*h+h2, c1*w+w2, r1*h+h2);
		c0=c1;r0=r1;dn=dn.n;}
	}catch(Exception ex){ex.printStackTrace();}

	if(mousePathOffset[0]!=0||mousePathOffset[1]!=0)
		g.drawOval(ppc*w+mousePathOffset[0], ppr*h+mousePathOffset[1], w/2, h/2);
	return mpc;
}

/*boolean runMousePath()
{if(mousePathFinder.start==null)
 {return false;}//mousePathTrgt[0]=mousePathTrgt[1]=-1;
 switch(mousePathFinder.start.dir)
	{case up:mousePathOffset[1]-=mousePathOffsetSpeed;
		if(mousePathOffset[1]<=-h)
		{mousePathFinder.start=mousePathFinder.start.p;
		 state.mov(ppr,ppc,Direction.up,true);
		 mousePathOffset[1]=mousePathOffset[0]=0;}break;
	case dn:mousePathOffset[1]+=mousePathOffsetSpeed;
	if(mousePathOffset[1]>=h)
	{mousePathFinder.start=mousePathFinder.start.p;
	 state.mov(ppr,ppc,Direction.dn,true);
	 mousePathOffset[1]=mousePathOffset[0]=0;}break;
	case rt:mousePathOffset[0]+=mousePathOffsetSpeed;
	if(mousePathOffset[0]>=w)
	{mousePathFinder.start=mousePathFinder.start.p;
	 state.mov(ppr,ppc,Direction.rt,true);
	 mousePathOffset[1]=mousePathOffset[0]=0;}break;
	case lf:mousePathOffset[0]-=mousePathOffsetSpeed;
	if(mousePathOffset[0]<=-w)
	{mousePathFinder.start=mousePathFinder.start.p;
	 state.mov(ppr,ppc,Direction.lf,true);
	 mousePathOffset[1]=mousePathOffset[0]=0;}break;
	}return true;}*/

 boolean runMousePath()
 {	if(q==null)return false;
	Direction dir=q.dir;
	int d=dir.vert?h:w,i=dir.vert?1:0,f=dir.vert?dir.dy:dir.dx;
	mousePathOffset[i]+=f*mousePathOffsetSpeed;
	if((!dir.postv&&mousePathOffset[i]<=-d)||(dir.postv&&mousePathOffset[i]>=d))
	{	state.mov(ppr,ppc,dir,true);q=q.n;
		mousePathOffset[1]=mousePathOffset[0]=0;
	}return true;
 }

//boolean runBlockMove(){return false;}

void startThread()
{p("startThread");
	if(thread==null)
	{p("new Thread");thread=new Thread(this);}
	if(!thread.isAlive())
	{thread.start();p("thread.start");}}

public void run()
{	Thread thrd=Thread.currentThread();
	p("run:begin");
	while(thrd==thread)try{
		boolean b=runMousePath();
		//b|=runBlockMove();
		Thread.sleep(5);
		if(!b)
		{thread=null;mousePathFinder=null;}
		repaint();
	}catch(Exception ex){ex.printStackTrace();}
	//thread=null;
	p("run:exit");}
}//class PathAnim

}//class SokobanSolver