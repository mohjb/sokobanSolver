//package soso;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class OldSokobanSolver extends JPanel
implements KeyListener,MouseListener,MouseMotionListener,ActionListener,Runnable
{private static final long serialVersionUID = -8615408389885762774L;
Thread thread;

JFrame frame;
JTextArea ta;
static JLabel status;
static int ppr,ppc,mr,mc;
//static List<Direction> mousePath=new LinkedList<Direction>();
State state=new State("XXXX\nX  X\nXODGX\nXXXXX",true);
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
{try{File f=new File("levels.levels");
 FileInputStream fr=new FileInputStream(f);
 byte[]b=new byte[(int)f.length()];
 fr.read(b);
 fr.close();
 String s=new String(b);
 p("loading levels.levels:\n"+s);
 int i=s.indexOf("\n\n"),n=s.length(),j=0;
 if(i==-1){levels=new String[1];levels[0]=s.trim();}else{
 LinkedList<String>ll=new LinkedList<String>();
 while(j<n&&s.charAt(j)=='\n')j++;
 while(i!=-1)
 {i+=2;
	ll.add(s.substring(j, i));j=i;
	while(j<n&&s.charAt(j)=='\n')j++;
	i=s.indexOf("\n\n",j);
 }if(j<n)ll.add(s.substring(j,n));
 levels=new String[ll.size()];
 ll.toArray(levels);}
 for(i=0;i<levels.length;i++)menu("level "+i);
}catch(Exception x){x.printStackTrace();}}

void menu(String s){
	JMenuItem mi=new JMenuItem(s);
	menu.add(mi);mi.addActionListener(this);}

 public OldSokobanSolver() {
	frame=new JFrame("OldSokobanSolver 2011, by mohamadjb@gmail.com");
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
	menu("mouse-click move(keyboard:M)");
	//menu("mousePath toggle");
	loadLevels();
	frame.setVisible(true);status("loaded");}

 static Map<String,BufferedImage>imgs=new HashMap<String,BufferedImage>();

 static BufferedImage img(String fileName){
	 BufferedImage r=imgs.get(fileName);
	 if(r==null)try{
		 imgs.put(fileName, r=ImageIO.read(new File(fileName)));
	 }catch(Exception x){x.printStackTrace();}return r;}

 public static void main(String[]args){new OldSokobanSolver();}

enum Cell{/**Wall obstacle*/X ,/**space*/_
	,/**Crater or block or box that can be pushed*/D
	,/**Goal location*/G ,/**player location*/O
	,/**player standing on a goal*/o
	,/**a crater or block or box on a goal*/d;
 BufferedImage img(){return OldSokobanSolver.img(
		(this==o?"og":this==d?"dg":toString())+".png");}
};
enum Direction{up,dn,rt,lf};

class DirNode
{/**the move from the previous state to this state*/
Direction d;DirNode p,n;
DirNode(Direction d,DirNode p){this.d=d;if((this.p=p)!=null)p.n=this;}
DirNode copy(){return copy(null);}
DirNode copy(DirNode n){DirNode r=new DirNode(d,null);r.p=p==null?p:p.copy(r);r.n=n;return r;}

/*void paint(Graphics g){
	g.drawString("dirPref:"+dirPref+" ,dimY:"+dimY+" ,pstv:"+pstv
		+" ,d:"+d+" ,stackState:"+stackState+" ,success:"+success
		//,v1:"+v1+" +" ,dx:"+dx+" ,dy:"+dy ,dir:"+dir+" +" ,r1:"+r1+" ,c1:"+c1
		,ppc*w+2,ppr*h+h2);
	if(parent!=null)
		lineArrow(g, parent.ppr, parent.ppc, ppr, ppc);
	if(dir[0]!=null)dir[0].paint(g);
	if(dir[1]!=null)dir[1].paint(g);
	if(dir[2]!=null)dir[2].paint(g);
	if(dir[3]!=null)dir[3].paint(g);
}//paint*/

}//class DirNode

class State
{Cell a[ ][ ];
 int Cols,/**count of craters on goals*/countd
 ,/**count of goals which are not filled*/countG
 ,/**count of Craters which are not on goal*/countD;

 DirNode d;

 /**nxt:if true s.next=this state and this.prev=s, otherwise this.next=s and s.prev=this*/
 public State(State p){copy(p);}

 public State(String p,boolean playerPosition){load(p,playerPosition);}

void load(String p,boolean playerPosition)
{String[]lines=p.split("\n");Cols=0;
 int ln=lines.length,li=0;
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
public State copy(Direction p){copy();d.d=p;
	int r=ppr,c=ppc;
	mov(ppr, ppc, p==p.up||p==p.dn, p==p.rt||p==p.dn, false);
	ppr=r;ppc=c;return this;}

public State copy(State p)
{if(a==null||a.length!=p.a.length)a=new Cell[p.a.length][];Cols=p.Cols;
	for(int row=0;row<a.length;row++)
	{	if(a[row]==null||a[row].length!=p.a[row].length)
			a[row]=new Cell[p.a[row].length];
		for(int col=0;col<a[row].length;col++)
			a[row][col]=p.a[row][col];
	}d=p.d;return this;}

/**returns a count which is the number of craters that are on not on any goal.*/
int updateCounts()
{countD=0;countd=0;countG=0;for(int row=0;row<a.length;row++)
 for(int col=0;col<a[row].length;col++)
 {Cell v=a[row][col];if(v==v.D)countD++;else if(v==v.d)countd++;else if(v==v.G)countG++;
 }return countD;}

Cell getCell(int row,int col)
{return row<0||col<0
	||a.length<=row
	||a[row].length<=col
	?null
	:a[row][col];}

Cell getCell(int row,int col,boolean dimY,boolean dirPstv)
{if(dimY)row+=dirPstv?1:-1;else col+=dirPstv?1:-1;
 return getCell(row, col);}

boolean isCanMove(int row,int col,boolean dimY,boolean dirPstv)
{	Cell v=getCell(row, col, dimY, dirPstv);
	if(v==null||v==Cell.X)return false;
	if(dimY)row+=dirPstv?1:-1;else col+=dirPstv?1:-1;
	if(v==Cell.D||v==Cell.d){Cell v2=getCell(row, col, dimY, dirPstv);
		return v2!=null&&v2!=Cell.X&&v2!=Cell.D&&v2!=Cell.d;}
	return true;}

boolean mov(int row,int col,boolean dimY,boolean dirPstv,boolean updatePlayerPosition)
{p("state.mov(row:"+row+" , col:"+col+" , dimY:"+dimY+" , dirPstv:"+dirPstv+")");
 if(!isCanMove(row, col, dimY, dirPstv))return false;
 if(saveUndo);//{State old=prev,nw=copy();nw.prev=old;old.next=nw;prev=nw;nw.next=this;}
 int r1=row,c1=col;
 if(dimY)r1+=dirPstv?1:-1;else c1+=dirPstv?1:-1;
 if(updatePlayerPosition){ppr=r1;ppc=c1;}
 Cell v0=getCell(row, col),v1=getCell(r1 , c1);
 a[row][col]=v0==Cell.o?Cell.G:Cell._;
 a[r1][c1]=v1==Cell.G||v1==Cell.d?Cell.o:Cell.O;
 if(v1==Cell.D||v1==Cell.d)
 {	p("state.move:crater:row1="+r1+" ,col1="+c1+" ,v1="+v1);
	int r2=r1,c2=c1;
	if(dimY)r2+=dirPstv?1:-1;else c2+=dirPstv?1:-1;
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

class PathFinder extends State
{PathFinder(State p){super(p);}
	int tr,tc;//DirNode pathStack;

 void pathClear(){d=null;}

 int size(){int r=0;DirNode p=d;while(p!=null){r++;p=p.p;}return r;}

 void push(Direction p){d=new DirNode(p,d);}

 void findPath(State p,int currentRow,int currentCol,int targetRow,int targetCol)
 {copy(p);pathClear();tr=targetRow;tc=targetCol;
	replace(Cell.D,Cell.d,Cell.X  ,Cell.G,Cell._  ,Cell.o,Cell.O);
	recurs(currentRow,currentCol);
 //find short-cuts in un-optimized mousePath.
 // checkMousePathShortCuts();
 }

void replace(Cell o,Cell v)
{for(int r=0;r<a.length;r++)
 for(int c=0;r<a[r].length;c++)
 if(a[r][c]==o)a[r][c]=v;}

void replace(
 Cell o1,Cell o12,Cell v1
 ,Cell o2,Cell v2  
 ,Cell o3,Cell v3)
{for(int r=0;r<a.length;r++)
 for(int c=0;c<a[r].length;c++)
 {Cell v=getCell(r, c);
	if(v==o1||v==o12)a[r][c]=v1;
	else if(v==o2)a[r][c]=v2;
	else if(v==o3)a[r][c]=v3;}}

boolean recurs(int r,int c)
{	if(r==tr&&c==tc)return true;
	a[r][c]=Cell.G;//Cell v1;
	int r1=r,c1=c,dy=tr-r1,dx=tc-c1;
	boolean isVertical=Math.abs(dy)>Math.abs(dx)
		,pstv=isVertical?dy>0:dx>0;

	//1st direction preference
	if(isVertical)r1+=pstv?1:-1;else c1+=pstv?1:-1;
	//v1=getCell(r1, c1);
	if(getCell(r1, c1)==Cell._)//v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{a[r1][c1]=Cell.O;
		if(recurs(r1,c1))
		{Direction d=isVertical
			?(pstv?Direction.dn:Direction.up)
			:(pstv?Direction.rt:Direction.lf);
			push( d);
			return true;}}

	//2nd direction preference, will use a perpendicular direction, and try to choose a +ve/-ve according to the goal dx or dy
	if(isVertical){r1=r;c1=c+(dx>0?1:-1);}else{c1=c;r1=r+(dy>0?1:-1);}
	//v1=getCell(r1, c1);
	if(getCell(r1, c1)==Cell._)//v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{a[r1][c1]=Cell.O;
	 if(recurs(r1,c1))
	 {Direction d=isVertical//(pstv?Direction.dn:Direction.up):(pstv?Direction.rt:Direction.lf);
			?(dx>0?Direction.rt:Direction.lf)
			:(dy>0?Direction.dn:Direction.up);
		push( d);
		return true;}}

	//3rd direction preference, will use a perpendicular direction, and choose -ve/+ve opposing the previous 2nd-direction
	if(isVertical){r1=r;c1=c+(dx>0?-1:1);}else{c1=c;r1=r+(dy>0?-1:1);}
	//v1=getCell(r1, c1);
	if(getCell(r1, c1)==Cell._)//v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{a[r1][c1]=Cell.O;
	 if(recurs(r1,c1))
	 {Direction d=!isVertical
			?(dy<0?Direction.dn:Direction.up)
			:(dx<0?Direction.rt:Direction.lf);
		push( d);
		return true;}}

	//4th direction preference
	if(!isVertical){r1=r;c1=c+(pstv?-1:1);}else{c1=c;r1=r+(pstv?-1:1);}
	//v1=getCell(r1, c1);
	if(getCell(r1, c1)==Cell._)//v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{a[r1][c1]=Cell.O;
	 if(recurs(r1,c1))
	 {Direction d=isVertical
			?(!pstv?Direction.dn:Direction.up)
			:(!pstv?Direction.rt:Direction.lf);
		push( d);
		return true;}}
	a[r][c]=Cell.D;
	return false;
 }
/**
void checkMousePathShortCuts1()
{LinkedList<Point>ll=new LinkedList<Point>();
 int n=mousePath.size(),y=mr,x=mc,i=n-1,j,count=0;
 Point p,q;Direction d;
 ll.add(p=new Point(x,y));
 while(i>=0)//r!=ppr&&c!=ppc)
 {	d=mousePath.get(i);
	if(d==Direction.up)y--;
	else if(d==Direction.dn)y++;
	else if(d==Direction.rt)x++;
	else if(d==Direction.lf)x--;
	ll.add(new Point(x,y));
	i--;
 }//while
 i=n-1;
 while(i>=0)
 {	p=ll.get(i);
 	j=i-2;
 	while(j>=0)
 	{	q=ll.get(j);
		d=(p.x==q.x&&p.y==q.y+1)?Direction.up
		 :(p.x==q.x&&p.y==q.y-1)?Direction.dn
		 :(p.y==q.y&&p.x==q.x+1)?Direction.rt
		 :(p.y==q.y&&p.x==q.x-1)?Direction.lf
		 :null;
		if(d!=null)
		{p("found short-cut between (index="+i+" y="+p.y+" x="+p.x+") and (index="+j+" y="+q.y+" x="+q.x+") ,direction="+d+" ,count="+(++count));
		 i=j+1;
		}
		j--;
	}//while
	i--;
 }//while
}//checkMousePathShortCuts

void checkMousePathShortCuts()
{short[][]a=mousePathShortCuts;
 if(a==null||a.length!=state.a.length)
	a=mousePathShortCuts=new short[state.a.length][];
 for(int r=0;r<a.length;r++)
 {if(a[r]==null||a[r].length!=state.a[r].length)
	a[r]=new short[state.a[r].length];
	for(int c=0;c<a[r].length;c++)
		a[r][c]=Short.MAX_VALUE;}
 int r=mr,c=mc,n=mousePath.size();
 a[r][c]=(short)(n<<2);
 for(int i=n-1;i>=0;i--)
 {Direction d=mousePath.get(i);
	if(d==Direction.up)r++;
	else if(d==Direction.dn)r--;
	else if(d==Direction.rt)c--;
	else if(d==Direction.lf)c++;
	a[r][c]=(short)(d.ordinal()+(i<<2));}
 r=mr;c=mc;
 for(int i=n-1;i>=0;i--)
 {Direction d=mousePath.get(i);
	if(d==Direction.up)r++;
	else if(d==Direction.dn)r--;
	else if(d==Direction.rt)c--;
	else if(d==Direction.lf)c++;
	a[r][c]=(short)(d.ordinal()+(i<<2));}
 
}//
*/short[][]mousePathShortCuts;

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
{if(pf==null)pf=new PathFinder(this);
	pf.pLoc(pr,pc);pf.pathClear();
	pf.findPath(this, pr, pc, tr,tc);
	return pf.d!=null;}

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
		for(int col=0;col<cols;col++)try
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
	}*/int mpc=0;
	try{DirNode dn=mousePathFinder==null?null:mousePathFinder.d;
		int r0=ppr,c0=ppc,r1=r0,c1=c0;
		while(dn!=null){mpc++;Direction d=dn.d;
		if(d==Direction.up)r1--;else
		if(d==Direction.dn)r1++;else
		if(d==Direction.rt)c1++;else
		if(d==Direction.lf)c1--;
		g.drawLine(c0*w+w2, r0*h+h2, c1*w+w2, r1*h+h2);
		c0=c1;r0=r1;dn=dn.p;}
	}catch(Exception ex){ex.printStackTrace();}

	if(mousePathOffsetX!=0||mousePathOffsetY!=0)
		g.drawOval(ppc*w+mousePathOffsetX, ppr*h+mousePathOffsetY, w/2, h/2);
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
	{mr=r;mc=c;updateMousePath();repaint();}}


void updateMousePath()
{if(mousePathFinder==null)mousePathFinder=new PathFinder(state);
 mousePathFinder.findPath(state, ppr, ppc, mr, mc);
}

PathFinder mousePathFinder;
BlockMoves blockMoves;
/*
void updateMousePath()
{	State s=
		((mousePathState!=null)
			?mousePathState.copy(state)
			:(mousePathState=state.copy()));
	mousePath.clear();
	replace(s  ,Cell.D,Cell.d,Cell.X  ,Cell.G,Cell._  ,Cell.o,Cell.O);
	computeMP(s,ppr,ppc);//else base.computeMP();
 
 //find short-cuts in un-optimized mousePath.
// checkMousePathShortCuts();
}

State mousePathState;
void replace(State s,Cell o,Cell v)
{for(int r=0;r<s.a.length;r++)
 for(int c=0;r<s.a[r].length;c++)
 if(s.a[r][c]==o)s.a[r][c]=v;}

void replace(State s,Cell o1,Cell o12,Cell v1,  Cell o2,Cell v2  ,Cell o3,Cell v3)
{for(int r=0;r<s.a.length;r++)
 for(int c=0;c<s.a[r].length;c++)
 {Cell v=s.getCell(r, c);
	if(v==o1||v==o12)s.a[r][c]=v1;
	else if(v==o2)s.a[r][c]=v2;
	else if(v==o3)s.a[r][c]=v3;}}

boolean computeMP(State s,int ppr,int ppc)
{	if(ppr==mr&&ppc==mc)return true;
	int r1=ppr,c1=ppc,dy=mr-r1,dx=mc-c1;
	boolean isVertical=Math.abs(dy)>Math.abs(dx)
		,pstv=isVertical?dy>0:dx>0;
	if(isVertical)r1+=pstv?1:-1;else c1+=pstv?1:-1;
	s.a[ppr][ppc]=Cell.G;
	Cell v1=s.getCell(r1, c1);//s.a[r1][c1];

	//1st direction preference
	if(v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{s.a[r1][c1]=Cell.O;
		if(computeMP(s,r1,c1)){
			Direction d=isVertical?(pstv?Direction.dn
					:Direction.up):(pstv?Direction.rt
					:Direction.lf);
			mousePath.add(0, d);
			return true;}
		//s.a[r1][c1]=Cell.D;
	}

	//2nd direction preference, will use a perpendicular direction, and the same pstv-flag value
	if(isVertical){r1=ppr;c1=ppc+(dx>0?1:-1);}else{c1=ppc;r1=ppr+(dy>0?1:-1);}
	v1=s.getCell(r1, c1);
	if(v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{s.a[r1][c1]=Cell.O;
	 if(computeMP(s,r1,c1)){
		Direction d=isVertical?//(pstv?Direction.dn:Direction.up):(pstv?Direction.rt:Direction.lf);
				(dx>0?Direction.rt:Direction.lf):(dy>0?Direction.dn:Direction.up);
		mousePath.add(0, d);
		return true;}
	 //s.a[r1][c1]=Cell.D;
	}

	//3rd direction preference
	if(isVertical){r1=ppr;c1=ppc+(dx>0?-1:1);}else{c1=ppc;r1=ppr+(dy>0?-1:1);}
	v1=s.getCell(r1, c1);
	if(v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{s.a[r1][c1]=Cell.O;
	 if(computeMP(s,r1,c1)){
		Direction d=!isVertical?(dy<0?Direction.dn
				:Direction.up):(dx<0?Direction.rt
				:Direction.lf);
		mousePath.add(0, d);
		return true;}
	 //s.a[r1][c1]=Cell.D;
	}
	
	//4th direction preference
	if(!isVertical){r1=ppr;c1=ppc+(pstv?-1:1);}else{c1=ppc;r1=ppr+(pstv?-1:1);}
	v1=s.getCell(r1, c1);
	if(v1!=null&&v1!=Cell.X&&v1!=Cell.D&&v1!=Cell.G)
	{s.a[r1][c1]=Cell.O;
	 if(computeMP(s,r1,c1)){
		Direction d=isVertical?(!pstv?Direction.dn
				:Direction.up):(!pstv?Direction.rt
				:Direction.lf);
		mousePath.add(0, d);
		return true;}
	 //s.a[r1][c1]=Cell.D;
	}
	s.a[ppr][ppc]=Cell.D;
	return false;
}*/


public void mouseClicked(MouseEvent e) 
{if(thread!=null){thread=null;mouseMoved(e);return;}
 if(blockMoves!=null&&blockMoves.canMove(mr, mc)){}
 if(mousePathFinder.d!=null)
	{//state.a[ppr][ppc]=state.a[ppr][ppc]==Cell.o?Cell.G:Cell._;
	 //state.a[mr][mc]=state.a[mr][mc]==Cell.G?Cell.o:Cell.O;
	 //ppr=mr;ppc=mc;mousePathFinder.pathClear();
	 startMousePath();
	 repaint();}
else{/*Direction d=(ppc==mc&&ppr==mr+1)?Direction.up
		 :(ppc==mc&&ppr==mr-1)?Direction.dn
		 :(ppr==mr&&ppc==mc-1)?Direction.rt
		 :(ppr==mr&&ppc==mc+1)?Direction.lf
		 :null;
	Cell v=state.getCell(mr,mc);
	if(d!=null&&v==Cell.D||v==Cell.d)
	{	state.mov(ppr, ppc, d==d.up||d==d.dn, d==d.dn||d==d.rt, true);
		repaint();
	}*/Cell v=state.getCell(mr,mc);
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
	if(e.isAltDown()&&kc==e.VK_SPACE)
		ta.setText(state.toString());
	boolean b=false;
	if(b=(e.isAltDown()&&kc==e.VK_ENTER))
		state.load(ta.getText(),true);else
	if(b=kc==e.VK_UP  )state.mov(ppr,ppc,true ,false,true);
	else if(b=kc==e.VK_DOWN)state.mov(ppr,ppc,true ,true ,true);
	else if(b=kc==e.VK_LEFT)state.mov(ppr,ppc,false,false,true);
	else if(b=kc==e.VK_RIGHT)state.mov(ppr,ppc,false,true,true);
	//else if(b=kc==e.VK_Z)state=state.prev;
	//if(kc==e.VK_F1){if(base!=null)base=null;else{base=top=new MousePathStackFrame(state.copy(), ppr, ppc);if(!b)updateMousePath();}}
	//if(kc==e.VK_F2&&top!=null)top.computeMP();
	if(kc==e.VK_G)design=Cell.G;
	if(kc==e.VK_X)design=Cell.X;
	if(kc==e.VK_D)design=Cell.D;
	if(kc==e.VK_SPACE)design=Cell._;
	if(kc==e.VK_P)design=Cell.O;
	if(kc==e.VK_M)design=null;
	if(b){if(design==null)updateMousePath();repaint();}}

public void actionPerformed(ActionEvent e)
{Object src=e.getSource();
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

void startMousePath(){mousePathTrgtR=mr;mousePathTrgtC=mc;startThread();}
void startBlockMove(){}

int mousePathOffsetX=0,mousePathOffsetY=0
,mousePathTrgtR=-1,mousePathTrgtC=-1
,mousePathOffsetSpeed=5;

boolean runMousePath()
{if(mousePathFinder.d==null)
 {mousePathTrgtR=mousePathTrgtC=-1;return false;}
 switch(mousePathFinder.d.d)
	{case up:mousePathOffsetY-=mousePathOffsetSpeed;
		if(mousePathOffsetY<=-h)
		{mousePathFinder.d=mousePathFinder.d.p;
		 state.mov(ppr,ppc,true,false,true);
		 mousePathOffsetY=mousePathOffsetX=0;}break;
	case dn:mousePathOffsetY+=mousePathOffsetSpeed;
	if(mousePathOffsetY>=h)
	{mousePathFinder.d=mousePathFinder.d.p;
	 state.mov(ppr,ppc,true,true,true);
	 mousePathOffsetY=mousePathOffsetX=0;}break;
	case rt:mousePathOffsetX+=mousePathOffsetSpeed;
	if(mousePathOffsetX>=w)
	{mousePathFinder.d=mousePathFinder.d.p;
	 state.mov(ppr,ppc,false,true,true);
	 mousePathOffsetY=mousePathOffsetX=0;}break;
	case lf:mousePathOffsetX-=mousePathOffsetSpeed;
	if(mousePathOffsetX<=-w)
	{mousePathFinder.d=mousePathFinder.d.p;
	 state.mov(ppr,ppc,false,false,true);
	 mousePathOffsetY=mousePathOffsetX=0;}break;
	}return true;}

boolean runBlockMove(){return false;}

void startThread()
{p("startThread");
	if(thread==null)
	{p("new Thread");thread=new Thread(this);}
	if(!thread.isAlive())
	{thread.start();p("thread.start");}}

public void run()
{	Thread thrd=Thread.currentThread();p("run:begin");
	while(thrd==thread)try{
		boolean b=runMousePath();
		b|=runBlockMove();
		Thread.sleep(5);
		if(!b)
			thread=null;
		repaint();
	}catch(Exception ex){ex.printStackTrace();}
	//thread=null;
	p("run:exit");}

}//class SokobanSolver
