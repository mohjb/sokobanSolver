package net.moh.spideract;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class SokoAct extends Activity
{@Override protected void onStart(){Log.d("onStart","start");super.onStart();}
 @Override protected void onPause(){Log.d("onPause","pause");thread=null;super.onPause();}
 @Override protected void onResume(){Log.d("onResume","resume");super.onResume();}
 @Override protected void onStop(){Log.d("onStop","stop");thread=null;super.onStop();}
 @Override public boolean onKeyDown(int keyCode, KeyEvent event)
 { Log.d("act.onKeyDown","event="+event);
		if(keyCode==KeyEvent.KEYCODE_MENU)
			{Log.d("act.onKeyDown","menu");}//menu.on(!menu.on());
		return super.onKeyDown(keyCode, event);}

 @Override public void onCreate(Bundle savedInstanceState)
 {	super.onCreate(savedInstanceState);
	//plyr=new Plyr();load(LEVELS[levelIndex=0]);
	setContentView(view=new MView(this));menu=new Menu();}

// static float distSqr(Node n, float x2,float y2){return distSqr(n.x, n.y, x2, y2);}
 static float distSqr( float x1,float y1, float x2,float y2)
 {float dx=x2-x1,dy=y2-y1;return dx*dx+dy*dy;}


class Menu
{Menu(){b=new Btn();
	b.text="level++";b.cx=.2f;b.cy=.2f;b.w=.1f;b.h=.1f;
	Btn n=b.next=new Btn();n.prev=b;
	b.text="level--";b.cx=.2f;b.cy=.8f;b.w=.1f;b.h=.1f;
	//(n.next=new Btn()).prev=n;n=n.next;b.text="restart";b.cx=.2f;b.cy=.8f;b.w=.1f;b.h=.1f;
	//(n.next=new Btn()).prev=n;n=n.next;b.text="undo";b.cx=.2f;b.cy=.8f;b.w=.1f;b.h=.1f;
	onResize(szw, szh);}

 public boolean touch(MotionEvent e)
 {	final int action = e.getAction();float x=e.getX(),y=e.getY();
	try{switch(action & MotionEvent.ACTION_MASK){
	case MotionEvent.ACTION_UP:case MotionEvent.ACTION_POINTER_UP:
		Btn n=b;while(n!=null){if(n.r.contains(x, y))//x0<=x&x<=n.x1 && n.y0<=y&&y<=n.y1)
		{	/*Leg[]a=null,b=plyr.legs;int z=plyr.legs.length;
			if("+".equals(n.text)	  ){a=new Leg[z+1];
				for(int i=0;i<z   ;i++){a[i]=b[i];b[i]=null;}
				a[z]=new Leg(z)   ;a[z].x=plyr.x;a[z].y=plyr.y;}else
			if("-".equals(n.text)&&z>1){a=new Leg[z-1];
				for(int i=0;i<z-1 ;i++){a[i]=b[i];b[i]=null;}}
			if(a!=null)plyr.legs=a;*/n=null;view.postInvalidate();
		}else n=n.next;}
 }}catch(Exception ex){Log.d("Menu.touch", "ex", ex);}return true;}

 void draw(Canvas g)
 {float w4=szw/2,h4=szh/2,x2=szw*1.5f,y2=szh*1.5f;
	//view.paint.setStrokeWidth(4);
	view.paint.setStyle(Style.FILL);
	view.paint.setColor(0x10ffffff);
	g.drawRect(w4-4, h4-4, x2+4, y2+4, view.paint);
	view.paint.setColor(0x80000000);
	g.drawRect(w4, h4, x2, y2, view.paint);
	view.paint.setStyle(Style.STROKE);
	Btn n=b;while(n!=null)
	{	g.drawRoundRect(n.r, 5, 5, view.paint);
		g.drawText(n.text, n.r.left, n.r.top, view.paint);
		n=n.next;
	}//if(n!=null)g.drawText("", n.r.left, n.r.top, view.paint);////number of legs="+plyr.legs.length
 }

 //Menu nxt,prv;public boolean _on;boolean on(){return _on;}boolean on(boolean p){if(_on!=p){if(_on)thread=null;else thrd();}return _on=p;}
 public Btn b;

 void onResize(int szw,int szh)
 {	Btn n=b;while(n!=null)
 	{	float cx=szw*n.cx,cy=szh*n.cy,w=szw*n.w/2,h=szh*n.h/2;
 		n.r.set(cx-w, cy-h, cx+w, cy+h);
 		n=n.next;}
 }//onResize

 class Btn
 {RectF r=new RectF();float cx,cy,w,h;Btn next,prev;String text;
  //void onTchUp(MotionEvent e){}
 }//class Btn
}//class Menu

 public class MView extends View
 {	public Paint paint;

 public MView(Context context)
 {super(context);paint=new Paint();paint.setStyle(Style.STROKE);szw=getWidth()/2 ; szh=getHeight()/2;}

 @Override protected void onDraw(Canvas g)
 {	g.drawARGB(250, 0, 0, 0  );
	super.onDraw(g);
	/*Tch t=view.tch;int rad=50;while(t!=null)
	{	paint.setColor(t.on()?0xff8080ff:0xa0808080);
		g.drawCircle(t.xIx, t.yIx, rad, paint);
	//	drawText(g,t.toString(), t.xIx-rad,(int) t.yIx+rad,18);
		t=t.next;}*/
	g(g);menu.draw(g);//if(menu.on())
	//drawText(g,"thrdCnt="+thrdCnt+",ThreadCount="+ThreadCount+",drwCount="+(++drwCount)+",evtCount="+evtCount,10,10,18);
 }//onDraw

 int drawText(Canvas g,String s,float x,int y,int yh)
 {int i=s==null?-1:s.indexOf("\n");
 	if(i!=-1){int j=0;do{
 			g.drawText(s.substring(j, i), x, y+=yh, paint);
 			i=s.indexOf("\n",j=i+1);
 		}while(i!=-1);
 		g.drawText(s.substring(j), x, y+=yh, paint);
 	}else g.drawText(s, x, y+=yh, paint);
	return y;}


 @Override public boolean onTouchEvent(MotionEvent e)
 {	//if(menu.on())return menu.touch(e);
	final int action = e.getAction();int i;Tch t;//,ix=e.getActionIndex()
	int ptCnt=e.getPointerCount();
	boolean dn=false,gm=false;//=state==State.game;
	try{switch(action & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:dn=true;
		case MotionEvent.ACTION_MOVE:
		for(i=0;i<ptCnt;i++)
		{	t=tch.id(e.getPointerId(i));
			t.xIx=e.getX(i);
			t.yIx=e.getY(i);tchCount++;
			if(gm)
			{	t.x=t.xIx-szw;//+ofstx
				t.y=t.yIx-szh;//+ofsty
				//t.ang=Math.atan2(t.y-plyr.y, t.x-plyr.x);
				t.on(true);}
			if(dn){tchCount=0;
				t.reset++;
				//if(gm)t.updtClosest(plyr);
				//if(t.n instanceof Leg)((Leg)t.n).t=t;
				}t.cntr++;}break;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_CANCEL:		
	case MotionEvent.ACTION_POINTER_UP:
		for(i=0;i<ptCnt;i++)
		{	t=tch.id(e.getPointerId(i));
			t.on(false);t.cntr++;
			/*if(t.n!=null)
			{	if(gm)
					t.n.launch(t.ang , plyr.x, plyr.y);
				t.n.t=null;t.n=null;}*/
		}/*if(!gm && tchCount==0)
		{	if(state==State.waiting)
			{	state=State.game;
				if(thread==null)
					thrd();
			}else
				load(LEVELS[levelIndex=((levelIndex+(state==State.pass?1:0))%LEVELS.length)]);
		}*/break;
 }}catch(Exception ex){Log.d("onTouchEvent", "ex", ex);}return true;}

 @Override protected void onSizeChanged(int w, int h, int oldw, int oldh)
 {	Log.d("onSizeChanged","w="+w+" ,h="+h+" ,oldw="+oldw+" ,oldh="+oldh);
	szw=w/2;szh=h/2;menu.onResize(w, h);
	super.onSizeChanged(w, h, oldw, oldh);}

 }//class MView


 MView view;int szw,szh,tchCount,levelIndex,ppr,ppc;Menu menu;
 Thread thread;PathFinder path;
Tch tch=new Tch(0);

public class Tch
{float xIx,yIx,x,y;//double ang;
	int id=-1,cntr,reset=0;//Leg n;
	Tch next;boolean _on;
	Tch(int pi){id=pi;}

	public boolean on(){return _on;}
	public boolean on(boolean p){if(p&&_on!=p)//updtClosest(plyr)
		;return _on=p;}
	
@Override public String toString(){return
		"{id="+id+" on="+_on+" cntr="+cntr+" reset="+reset
		+"\nxIx="+xIx+" yIx="+yIx+" @"+hashCode()
		//+"\nang="+ang
		+" ( "+x+" , "+y+" )"
		//+"\nn="+n
		+"}";}

	Tch id(int i){if(id>=i)return this;if(next==null)next=new Tch(id+1);return next.id(i);}

	/*void updtClosest(Plyr p)
	{	n=null;//if(id>0)n=null;
		float c=Float.MAX_VALUE,d;
		for(int i=0;i<p.legs.length;i++)
		{	Leg e=p.legs[i];d=distSqr(e, x, y);
			if(c>d && (e.t==null||e.t==this))
			{c=d;n=e;e.t=this;}}}*/

}//class Tch

void p(Object...p){}
 void g(Canvas g)
 {	//g.translate(-ofstx+szw, -ofsty+szh);
	Tch t=tch;int rad=60;while(t!=null)
	{	view.paint.setColor(t.on()?0xff40f0f0:0xa0404040);
		g.drawCircle(t.x, t.y, rad, view.paint);
		/*g.drawLine(plyr.x, plyr.y, 
		 (float)(plyr.x+plyr.legs[0].LaunchForce*Math.cos(t.ang)),
		 (float)(plyr.y+plyr.legs[0].LaunchForce*Math.sin(t.ang)), view.paint);
		t=t.next;}
	WallNode n=wHead;
	view.paint.setARGB(255,240,240,240);do{
		g.drawLine(n.x, n.y, n.next.x, n.next.y, view.paint);
		n=n.next;}while(n!=wHead);
	view.paint.setARGB(255,100,250,100);
	g.drawCircle(startx, starty, 40, view.paint);
	g.drawCircle(startx, starty, 35, view.paint);
	view.paint.setARGB(255,180,255,180);
	g.drawCircle(goalx, goaly, 40, view.paint);
	g.drawCircle(goalx, goaly, 35, view.paint);
	for(int i=0;i<plyr.legs.length;i++)
	{	Leg l=plyr.legs[i];
		view.paint.setARGB(255, (i%2)==0?200:255, (i%2)==0?200:0, (i%2)==0?100:0);
		if(l.t!=null && l.t.n==l&&state==State.game)
			g.drawLine(l.x, l.y
			, l.t.x//Ix+ofstx-szw
			, l.t.y//Ix+ofsty-szh
			, view.paint);
		g.drawLine(l.x, l.y, plyr.x, plyr.y, view.paint);
		g.drawCircle(l.x , l.y, 5, view.paint);
		g.drawLine(l.x, l.y, l.x+(float)l.inrx, l.y+(float)l.inry, view.paint);
	}view.paint.setColor(0xffe0e0ff);view.paint.setStyle(Style.FILL);
	g.drawCircle(plyr.x, plyr.y, 20, view.paint);view.paint.setStyle(Style.STROKE);
	g.drawLine(plyr.x, plyr.y, plyr.x+(float)plyr.inrx*10, plyr.y+(float)plyr.inry*10, view.paint);
	g.translate(ofstx-szw, ofsty-szh);
	/*view.drawText(g,"plyr.inr( "+plyr.inrx+" , "+plyr.inry+" )\nleg0( "
			+plyr.legs[0].inrx+" , "+plyr.legs[0].inry+" )\nleg1( "
			+plyr.legs[1].inrx+" , "+plyr.legs[1].inry+" )", 3, 15, 18);* /
	if(state!=State.game){view.paint.setColor(0xff80ff80);
		g.drawText(state.toString(), szw, szh/2, view.paint);*/}}


 void thrd()
 {Log.d("thrd", "code0");
 if(thread==null){
  thread=new Thread()
 {@Override public void run()
 {Thread t=Thread.currentThread();while(t==thread)try
 {Thread.sleep(75);
  //compute();
  view.postInvalidate();
 }catch(Exception x){Log.d("Thrd", "ex:",x);
 }Log.d("Thread", "4:end:after-loop");}};
  thread.start();
  }}

//public enum Cell {_(true),O(true),o(true),G(true),D,d,X;public boolean free;Cell(boolean f){free=f;}Cell(){this(false);}}

enum Cell{/**Wall obstacle*/X ,/**space*/_(true)
	,/**Crater or block or box that can be pushed*/D
	,/**Goal location*/G(true) ,/**player location*/O(true)
	,/**player standing on a goal*/o(true)
	,/**a crater or block or box on a goal*/d;
public boolean free;Cell(){this(false);}
Cell(boolean f){free=f;}/*loadImage();

BufferedImage img;BufferedImage img(){return img;}

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
	g.drawString(s, 0, 10);}}*/

}//enum Cell
 
 public enum Dir
 {	 up(true ,false, 0,-1)
	,dn(true ,true , 0, 1)
	,rt(false,true , 1, 0)
	,lf(false,false,-1, 0);
	public boolean vert,postv;public int dy,dx;
	Dir(boolean v,boolean p,int x,int y){vert=v;postv=p;dx=x;dy=y;}
	static Dir dir(int x1,int y1,int x2,int y2)
	{int dx=x2-x1,dy=y2-y1;
		return Math.abs(dx)>Math.abs(dy)
			?(dx<0?lf:rt):(dy<0?up:dn);}
	static Dir dir(Node a,Node b){return dir(a.c,a.r , b.c,b.r);}
	Dir opposite(){return this==dn?up:this==up?dn:this==lf?rt:lf;}
 }//enum Direction

class State
{public Cell[][]a;
 public int Cols,/**count of craters on goals*/countd
	,/**count of goals which are not filled*/countG
	,/**count of Craters which are not on goal*/countD;

 void load(String p){}
 State(String p){}
 State(State p){}
 
 public State(String p,boolean playerPosition){load(p,playerPosition);}

void load(String p,boolean playerPosition)
{String[]lines=p.split("\n");Cols=0;
 int ln=lines.length,li=0;if(path!=null)path.grid=null;
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
public State copy(Dir p){copy();//dirnode.dir=p;
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
	}return this;}

/**returns a count which is the number of craters that are on not on any goal.*/
int updateCounts()
{countD=0;countd=0;countG=0;for(int row=0;row<a.length;row++)
 for(int col=0;col<a[row].length;col++)
 {Cell v=a[row][col];if(v==Cell.D)countD++;else if(v==Cell.d)countd++;else if(v==Cell.G)countG++;
 }return countD;}

public Cell getCell(int row,int col)
{return row<0||col<0
	||a.length<=row
	||a[row].length<=col
	?null
	:a[row][col];}

boolean mov(int row,int col,Dir dir,boolean updatePlayerPosition)//,boolean dimY,boolean dirPstv
{p("state.mov(row:"+row+" , col:"+col+" , dir:"+dir+")");
 if(!isCanMove(row, col, dir))return false;
 //if(saveUndo);//{State old=prev,nw=copy();nw.prev=old;old.next=nw;prev=nw;nw.next=this;}
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

boolean isCanMove(int row,int col,Dir dir)
{	Cell v=getCell(row+dir.dy, col+dir.dx);
	if(v==null||v==Cell.X)return false;
	if(dir.vert)row+=dir.dy;else col+=dir.dx;
	if(v==Cell.D||v==Cell.d){Cell v2=getCell(row+dir.dy, col+dir.dx);
		return v2!=null&&v2.free;}//!=Cell.X&&v2!=Cell.D&&v2!=Cell.d;
 return true;
}

}//class State

public class Node{Dir dir;Node p,n;int r,c,
estimated=Integer.MAX_VALUE,pathLength=Integer.MAX_VALUE;

public String locStr(){return "r:"+r+" ,c:"+c;}
@Override public String toString()
{return "{dir:"+dir+" ,"+locStr()
	+" ,pathLength:"+pathLength+" ,estimated:"+estimated
	+" ,p:"+(p==null?"null":"{"+p.locStr()+"}")
	+" ,n:"+(n==null?"null":"{"+n.locStr()+"}")+"}";}

Node(int r,int c){init(r,c);}
Node init(int r,int c){loc(r,c);n=p=null;dir=null;estimated=pathLength=Integer.MAX_VALUE;return this;}
void loc(int row,int col){r=row;c=col;}

/**returns next, unlinks this.*/
Node dequeue(){if(n!=null)n.p=p;if(p!=null)p.n=n;Node r=n;n=p=null;return r;}

/**links x after this ;returns x*/
Node link(Node x){if(x!=null){if(x.p!=null||x.n!=null)x.dequeue();x.p=this;x.n=n;if(n!=null)n.p=x;n=x;}return x;}

/**links x Before this ;returns x*/
Node linkBefore(Node x){if(x!=null){if(x.p!=null||x.n!=null)x.dequeue();x.n=this;x.p=p;if(p!=null)p.n=x;p=x;}return x;}

/**find estimate node*/
Node findE(int e,Node p)
{Node n=this;
while(e>n.n.estimated && e<p.estimated){n=n.n;p=p.p;}
return e>=p.estimated?p:n;}

/*grid distance*/int dist(Node b){return Math.abs(r-b.r)+Math.abs(c-b.c);}


}//class Node

public class PathFinder
{Node start,target,q,grid[][];State s;
 boolean find(State ps,int startRow,int startCol,int targetRow,int targetCol){s=ps;return find();}
 boolean find()
 {
	 return false;
 }
}//class PathFinder

static final String[]Levels=
{
};

}//class SpiderAct
