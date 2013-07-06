package com.artifex.mupdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class Crop_Canvas extends ImageView {

	private final static int PRESS_LB = 0;//表示左下角矩形框
	private final static int PRESS_LT = 1;//表示左上角矩形框
	private final static int PRESS_RB = 2;//表示右下角矩形框
	private final static int PRESS_RT = 3;//表示右上角矩形框

	private Bitmap bitMap = null;				//原始图片
	private RectF src = null;					//经过比例转换后的裁剪区域
	private RectF dst = null;					//图片显示区域，也就是drawBitmap函数中的目标dst
	private RectF ChooseArea = null;				//选择区域				
	private Paint mPaint = null;				//画笔
	private Matrix matrix = null;				//矩阵
	
	private int mx = 0;							//存储触笔移动时，之前�?��的触笔的x坐标
	private int my = 0;							//存储触笔移动时，之前�?��的触笔的y坐标
	private boolean touchFlag = false;			//触笔是否在屏幕之�?
	private boolean cutFlag = false;			//是否点击了menu上的裁剪按钮
	private int recFlag = -1;					//用来存储触笔点击了哪个小矩形框（改变选择区域大小的小矩形框）
	private boolean firstFlag = false;
	
	private RectF recLT = null;					//左上角的小矩形框
	private RectF recRT = null;					//右上角的小矩形框
	private RectF recLB = null;					//左下角的小矩形框
	private RectF recRB = null;					//右下角的小矩形框
	private static final int LEFT_AREA_ALPHA = 50 * 255 / 100;
	private RectF leftRectL = null;
	private RectF leftRectR = null;
	private RectF leftRectT = null;
	private RectF leftRectB = null;
	private Paint leftAreaPaint = null;
	
	public Crop_Canvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}
	
	public Crop_Canvas(Context context) {
		super(context);
		this.init();
	} 
	
	public void init(){
		cutFlag = true;
		recLT = new RectF();
		recLB = new RectF();
		recRT = new RectF();
		recRB = new RectF();
		dst = new RectF();
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);      //将画笔的风格改为空心
		ChooseArea = new RectF();
		this.setPressRecLoc();
		src = null;
		firstFlag = true;
		
		//选择框之外的灰色区域，分成四个矩形框
		
		leftAreaPaint = new Paint();
		leftAreaPaint.setStyle(Paint.Style.FILL);
		leftAreaPaint.setAlpha(Crop_Canvas.LEFT_AREA_ALPHA);
	}
	
	public void setBitmap(Bitmap bitmap){
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		src = new RectF(0,0,bd.getIntrinsicWidth(),bd.getIntrinsicHeight());
		this.bitMap = bitmap.copy(Config.ARGB_8888, true);
		
		this.setImageBitmap(bitMap);
		leftRectB = new RectF();
		leftRectL = new RectF();
		leftRectR = new RectF();
		leftRectT = new RectF();
	}
	
	public void imageScale(){
		matrix = this.getImageMatrix();
		matrix.mapRect(dst, src);
		int padding = this.getPaddingBottom();
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();
		//dst.set(dst.left+padding,dst.top+padding,dst.right+padding,dst.bottom+padding);
		dst.set(dst.left+20,dst.top+20,width-20,height - 40);
		ChooseArea = new RectF(dst);
		this.setPressRecLoc();
	}
	
	//裁剪出�?择区域里的图�?
	//之前要做比例转换，因为，你�?择的区域是针对比例转换后的图�?
	//�?��要使用ChooseArea的数值做比例转换，然后，使用这些数�?重新设置rec的大�?
	public Bitmap getSubsetBitmap(){
		float ratioWidth = bitMap.getWidth()/(float)(dst.right-dst.left);
		float ratioHeight = bitMap.getHeight()/(float)(dst.bottom - dst.top);
		int left = (int)((ChooseArea.left - dst.left) * ratioWidth);
		int right = (int)(left + (ChooseArea.right - ChooseArea.left) * ratioWidth);
		int top = (int)((ChooseArea.top - dst.top) * ratioHeight);
		int bottom = (int)(top + (ChooseArea.bottom - ChooseArea.top) * ratioHeight);
		src = new RectF(left,top,right,bottom);
		firstFlag = true;
		set_LeftArea_Alpha();
		return Bitmap.createBitmap(bitMap, left, top, right-left, bottom-top);
	}
	
	//获得ChooseArea对象
	public RectF getChooseArea(){
		return ChooseArea;
	}
	
	//移动选择区域，�?择区域是不能从图片区域里移出去的
	public void moveChooseArea(int move_x,int move_y){
		if(ChooseArea.left + move_x >= dst.left && ChooseArea.right + move_x <= dst.right
		&& ChooseArea.top + move_y >= dst.top && ChooseArea.bottom + move_y <= dst.bottom){
			ChooseArea.set(ChooseArea.left + move_x,ChooseArea.top+move_y
					,ChooseArea.right + move_x,ChooseArea.bottom+move_y);
		}else{
			if(ChooseArea.left + move_x < dst.left){
				ChooseArea.set(dst.left,ChooseArea.top
						,ChooseArea.right+dst.left-ChooseArea.left,ChooseArea.bottom);
			}
			if(ChooseArea.right + move_x > dst.right){
				ChooseArea.set(ChooseArea.left+dst.right-ChooseArea.right,ChooseArea.top
						,dst.right,ChooseArea.bottom);
			}
			
			if(ChooseArea.top + move_y < dst.top){
				ChooseArea.set(ChooseArea.left,dst.top
						,ChooseArea.right,ChooseArea.bottom+dst.top-ChooseArea.top);
			}
			
			if(ChooseArea.bottom + move_y > dst.bottom){
				ChooseArea.set(ChooseArea.left,ChooseArea.top+dst.bottom-ChooseArea.bottom
						,ChooseArea.right,dst.bottom);
			}
		}
		this.setPressRecLoc();
		mPaint.setColor(Color.GREEN);
		this.invalidate();
	}
	
	public boolean onTouchEvent(MotionEvent event){
		mPaint.setColor(Color.RED);
		
		//点击了裁剪按钮之后才会执行以下事�?
    	if(event.getAction() == MotionEvent.ACTION_DOWN && cutFlag){
    		//System.out.println(event.getX() + "," + event.getY());
    		//判断触笔是否在裁剪区域内，也就是ChooseArea�?
    		//如果是，整个区域随着鼠标移动而移�?
    		mx = (int)event.getX();
			my = (int)event.getY();
    		if(this.judgeLocation(mx,my)){
    			touchFlag = true;
    			mPaint.setColor(Color.GREEN);
    			this.invalidate();
    			return true;
    		}else{
    			//不在裁剪区域内，就判断触笔是否在改变区域大小的小矩形框之�?
    			if(this.findPresseddst((int)event.getX(), (int)event.getY())){
	    			touchFlag = true;
	    			mPaint.setColor(Color.RED);
	    			return true;
    			}
    		}
    	}
    	
    	if(event.getAction() == MotionEvent.ACTION_MOVE && touchFlag){
    		//判断是否点击了哪个个小矩形框
    		if(this.isOutOfArea((int)event.getX(), (int)event.getY())){
    			return true;
    		}
    		
    		//如果选择区域大小跟图像大小一样时，就不能移动
    		if(ChooseArea.left == dst.left && ChooseArea.top == dst.top &&
    		   ChooseArea.right == dst.right && ChooseArea.bottom == dst.bottom){
    		}else{
    			this.moveChooseArea((int)event.getX() - mx, (int)event.getY() - my);
    			mx = (int)event.getX();
    			my = (int)event.getY();
    		}
    	}
    	
    	//触笔移出屏幕时，将一些变量设回初�?
    	if(event.getAction() == MotionEvent.ACTION_UP){
    		recFlag = -1;
    		this.invalidate();
    		touchFlag = false;
    	}
    	
    	return super.onTouchEvent(event);
    }
	
	
	
	//判断是否要超出图片区域，这个函数会调用下面四个press打头的函�?
	//这个函数也会重绘整个画布，也就是选择区域会随�?��标的移动改变
	private boolean isOutOfArea(int x,int y){
		switch(recFlag){
		case Crop_Canvas.PRESS_LB:
			this.pressLB(x - mx, y - my);
			break;
		case Crop_Canvas.PRESS_LT:
			this.pressLT(x - mx, y - my);
			break;
		case Crop_Canvas.PRESS_RB:
			this.pressRB(x - mx, y - my);
			break;
		case Crop_Canvas.PRESS_RT:
			this.pressRT(x - mx, y - my);
			break;
		default:return false;
		}
		mx = x;
		my = y;
		this.invalidate();
		return true;
	}
	
	//找到点击了哪个矩形框（改变�?择区域大小的小矩形框�?
	//这个是在MotionEvent.ACTION_DOWN这个动作时执行的
	//是为了在MotionEvent.ACTION_MOVE的时候，知道应该移动哪个小矩形框
	public boolean findPresseddst(int x,int y){
		boolean returnFlag = false;
		if(this.isInRect(x, y, recLB)){
			recFlag = Crop_Canvas.PRESS_LB;
			returnFlag = true;
		}else if(this.isInRect(x, y, recLT)){
			recFlag = Crop_Canvas.PRESS_LT;
			returnFlag = true;
		}else if(this.isInRect(x, y, recRB)){
			recFlag = Crop_Canvas.PRESS_RB;
			returnFlag = true;
		}else if(this.isInRect(x, y, recRT)){
			recFlag = Crop_Canvas.PRESS_RT;
			returnFlag = true;
		}
		
		return returnFlag;
	}
	
	public boolean isInRect(int x,int y,RectF rect){
		if(x >= rect.left -20 && x <= rect.right + 20 && y > rect.top - 20 && y < rect.bottom + 20){
			return true;
		}
		return false;
	}
	
	//点击角落矩形框改变�?择区域大小时，不能超过图片所在的区域
	//下面以press打头的四个函数就是判断是否超出图片区�?
	//如果超出了，就移动不�?
	//不超出按照触笔移动的距离来移动小矩形�?
	
	//pressLB是当点击左下角小矩形框改变大小时是否超出图片区域
	//如果超出就将left与bottom的�?设为图片区域的left和bottom
	private void pressLB(int x,int y){
		float left = ChooseArea.left + x;
		float right = ChooseArea.right;
		float top = ChooseArea.top;
		float bottom = ChooseArea.bottom + y;
		if(left <= right - 30 && left >= dst.left && bottom <= dst.bottom && bottom >= top + 30){
				ChooseArea.set(left,top,right,bottom);
		}else{
			if(left + x < dst.left){
				left = dst.left;
			}
			
			if(bottom + y > dst.bottom){
				bottom = dst.bottom;
			}
			
			if(ChooseArea.left + x > ChooseArea.right - 30){
				left = ChooseArea.right - 30;
			}
			
			if(ChooseArea.bottom + y < ChooseArea.top + 30){
				bottom = ChooseArea.top + 30;
			}
			ChooseArea.set(left,top,right,bottom);
		}
		this.setPressRecLoc();
	}
	
	//pressLT是当点击左上角小矩形框改变大小时是否超出图片区域
	//如果超出就将left与top的�?设为图片区域的left和top
	private void pressLT(int x,int y){
		float left = ChooseArea.left + x;
		float right = ChooseArea.right;
		float top = ChooseArea.top + y;
		float bottom = ChooseArea.bottom;
		if(left <= right - 30 && left >= dst.left && top <= bottom - 30 && top >= dst.top){
			ChooseArea.set(left,top,right,bottom);
		}else{
			if(left < dst.left){
				left = dst.left;
			}
			
			if(top < dst.top){
				top = dst.top;
			}
			
			if(left > right - 30){
				left = right - 30;
			}
			
			if(top > bottom - 30){
				top = bottom - 30;
			}
			ChooseArea.set(left,top,right,bottom);
		}
		this.setPressRecLoc();
	}
	
	//pressRT是当点击右上角小矩形框改变大小时是否超出图片区域
	//如果超出就将right与top的�?设为图片区域的right和top
	private void pressRT(int x,int y){
		float left = ChooseArea.left;
		float right = ChooseArea.right + x;
		float top = ChooseArea.top + y;
		float bottom = ChooseArea.bottom;
		
		if(right <= dst.right && right >= left + 30 && top <= bottom - 30 && top >= dst.top){
			ChooseArea.set(left,top,right,bottom);
		}else{
			if(right > dst.right){
				right = dst.right;
			}
			
			if(top < dst.top){
				top = dst.top;
			}
			
			if(right < left + 30){
				right = left + 30;
			}
			
			if(top > bottom - 30){
				top = bottom - 30;
			}
			ChooseArea.set(left,top,right,bottom);
		}
		this.setPressRecLoc();
	}
	
	//pressRB是当点击右下角小矩形框改变大小时是否超出图片区域
	//如果超出就将right与bottom的�?设为图片区域的right和bottom
	private void pressRB(int x,int y){
		float left = ChooseArea.left;
		float right = ChooseArea.right + x;
		float top = ChooseArea.top;
		float bottom = ChooseArea.bottom + y;
		
		if(right<= dst.right && right >= left + 30 && bottom <= dst.bottom && bottom >= top + 30){
			ChooseArea.set(left,top,right,bottom);
		}else{
			if(right > dst.right){
				right = dst.right;
			}
			
			if(bottom > dst.bottom){
				bottom = dst.bottom;
			}
			
			if(right < left + 30){
				right = left + 30;
			}
			
			if(bottom < top + 30){
				bottom = top + 30;
			}
			ChooseArea.set(left,top,right,bottom);
		}
		this.setPressRecLoc();
	}
	
	//每次改变选择区域矩形的大小或者移动，各角落上的小矩形也要改变它的Location
	private void setPressRecLoc(){
		recLT.set(ChooseArea.left-5,ChooseArea.top-5 , ChooseArea.left+5, ChooseArea.top+5);
		recLB.set(ChooseArea.left-5,ChooseArea.bottom-5 , ChooseArea.left+5, ChooseArea.bottom+5);
		recRT.set(ChooseArea.right-5,ChooseArea.top-5 , ChooseArea.right+5, ChooseArea.top+5);
		recRB.set(ChooseArea.right-5,ChooseArea.bottom-5 , ChooseArea.right+5, ChooseArea.bottom+5);
	}
	
	//判断触笔是否在�?择区域内
	public boolean judgeLocation(float x,float y){
    	float start_x = this.getChooseArea().left;
    	float start_y = this.getChooseArea().top;
    	float last_x = this.getChooseArea().right;
    	float last_y = this.getChooseArea().bottom;
    	//System.out.println("chubi:" + x + "," + y);
    	//System.out.println(start_y + "," + last_y);
    	if(x > start_x+10 && x < last_x-10 && y > start_y+10 && y < last_y-10){
    		return true;
    	}
    	return false;
    }
	
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		if(firstFlag){
			this.imageScale();
			firstFlag = false;
			mPaint.setColor(Color.RED);
			System.out.println("Width: " + (dst.right - dst.left));
			System.out.println("Height: " + (dst.bottom - dst.top));
			System.out.println("Width: " + this.getDrawable().getIntrinsicWidth());
			System.out.println("Height: " + this.getDrawable().getIntrinsicHeight());
		}else{
			set_LeftArea_Alpha();
		}
		canvas.drawRect(ChooseArea, mPaint);
		mPaint.setColor(Color.BLUE);
		canvas.drawRect(recLT, mPaint);
		canvas.drawRect(recLB, mPaint);
		canvas.drawRect(recRT, mPaint);   
		canvas.drawRect(recRB, mPaint);
		
		canvas.drawRect(leftRectL, leftAreaPaint);
		canvas.drawRect(leftRectR, leftAreaPaint);
		canvas.drawRect(leftRectT, leftAreaPaint);
		canvas.drawRect(leftRectB, leftAreaPaint);
		
	}
	
	public void set_LeftArea_Alpha(){
		leftRectL.set(dst.left, dst.top, ChooseArea.left, dst.bottom);
		leftRectR.set(ChooseArea.right,dst.top,dst.right,dst.bottom);
		leftRectT.set(ChooseArea.left, dst.top, ChooseArea.right, ChooseArea.top);
		leftRectB.set(ChooseArea.left,ChooseArea.bottom,ChooseArea.right,dst.bottom);
	} 
}
