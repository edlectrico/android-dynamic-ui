package es.deusto.deustotech.dynamicui.components;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Vector;

import es.deusto.deustotech.dynamicui.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService.Insets;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class ProxyView extends View {

	public final static String NAMESPACE = null;
	private Paint mTextPaint;
	private int mAscent;
	private String widgetType; // Button, TextView, etc.

	@SuppressWarnings("serial")
	public static class ProxyException extends RuntimeException {

		public ProxyException() {
			super();
		}

		public ProxyException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public ProxyException(String detailMessage) {
			super(detailMessage);
		}

		public ProxyException(Throwable throwable) {
			super(throwable);
		}
	}

	private View impl;
	private String mText = "testing";
	private boolean mShowText = false;

	public ProxyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		build(context, attrs, defStyle, true);
	}

	public ProxyView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.widgetType = attrs.getAttributeValue(NAMESPACE, "widget");

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.ProxyView, 0, 0);

		try {
			mShowText = a.getBoolean(R.styleable.ProxyView_showText, false);
			mText = a.getString(R.styleable.ProxyView_text);
		} finally {
			a.recycle();
		}

		build(context, attrs, 0, false);
	}

	public View getRealView() {
		return impl;
	}

	/**
	 * @return the 'widget' value from the adaptable element in the layout to be
	 *         inserted as a key in the corresponding HashMap of components
	 */
	public String getWidgetType() {
		return widgetType;
	}

	private void build(Context context, AttributeSet attrs, int defStyle,
			boolean styleProvided) {
		final String widgetName = attrs.getAttributeValue(NAMESPACE, "widget");
		this.impl = WidgetFactory.createView(widgetName, context, attrs,
				defStyle, styleProvided);
		callPendingCalls();

		// TODO: what is this code for? It seems to not make any different in
		// the final UI result
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		// Must manually scale the desired text size to match screen density
		mTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
		mTextPaint.setColor(0xFF000000);
		setPadding(3, 3, 3, 3);
	}

	public static String jsonToStringFromAssetFolder(final String fileName,
			Context context) throws IOException {
		AssetManager manager = context.getAssets();
		InputStream file = manager.open(fileName);

		byte[] data = new byte[file.available()];
		file.read(data);
		file.close();
		return new String(data);
	}

	public boolean isShowText() {
		return mShowText;
	}

	public void setShowText(boolean showText) {
		mShowText = showText;
		invalidate();
		requestLayout();
	}

	private static class CallData {
		private String methodName;
		private Class<?>[] types;
		private Object[] args;

		CallData(String methodName, Class<?>[] types, Object[] args) {
			this.methodName = methodName;
			this.types = types;
			this.args = args;
		}
	}

	private final static ThreadLocal<Vector<CallData>> methodsCall = new ThreadLocal<Vector<CallData>>();

	private void registerCall(String methodName, Class<?>[] types,
			Object... args) {
		Vector<CallData> localMethodsToCall = methodsCall.get();
		if (localMethodsToCall == null) {
			localMethodsToCall = new Vector<CallData>();
			methodsCall.set(localMethodsToCall);
		}

		System.out.println("Calling " + methodName);
		localMethodsToCall.add(new CallData(methodName, types, args));
	}

	public void callPendingCalls() {
		final Vector<CallData> localMethodsToCall = methodsCall.get();
		for (CallData callData : localMethodsToCall) {
			try {
//				System.out.println("Now calling seriously"
//						+ callData.methodName);
				getMethodInParents(callData.methodName, callData.types).invoke(
						this.impl, callData.args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Method wrapMethod(Method method) {
		method.setAccessible(true);
		return method;
	}

	@SuppressWarnings("unchecked")
	private Method getMethodInParents(String name, Class<?>... types)
			throws NoSuchMethodException {
		Class<? extends View> implClass = this.impl.getClass();
		while (!implClass.equals(View.class)) {
			try {
				return wrapMethod(implClass.getDeclaredMethod(name, types));
			} catch (NoSuchMethodException e) {
				implClass = (Class<? extends View>) implClass.getSuperclass();
			}
		}
		return wrapMethod(implClass.getDeclaredMethod(name, types));
	}

	// /////////
	protected void initializeFadingEdge(TypedArray a) {
		if (this.impl == null) {
			registerCall("initializeFadingEdge",
					new Class<?>[] { TypedArray.class }, a);
			return;
		}

		try {
			getMethodInParents("initializeFadingEdge", TypedArray.class)
					.invoke(this.impl, a);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'initializeFadingEdge' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setFadingEdgeLength(int length) {
		if (this.impl == null) {
			registerCall("setFadingEdgeLength",
					new Class<?>[] { Integer.TYPE }, length);
			return;
		}

		try {
			getMethodInParents("setFadingEdgeLength", Integer.TYPE).invoke(
					this.impl, length);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setFadingEdgeLength' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void initializeScrollbars(TypedArray a) {
		if (this.impl == null) {
			registerCall("initializeScrollbars",
					new Class<?>[] { TypedArray.class }, a);
			return;
		}

		try {
			getMethodInParents("initializeScrollbars", TypedArray.class)
					.invoke(this.impl, a);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'initializeScrollbars' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setVerticalScrollbarPosition(int position) {
		if (this.impl == null) {
			registerCall("setVerticalScrollbarPosition",
					new Class<?>[] { Integer.TYPE }, position);
			return;
		}

		try {
			getMethodInParents("setVerticalScrollbarPosition", Integer.TYPE)
					.invoke(this.impl, position);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setVerticalScrollbarPosition' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnFocusChangeListener(OnFocusChangeListener l) {
		if (this.impl == null) {
			registerCall("setOnFocusChangeListener",
					new Class<?>[] { OnFocusChangeListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnFocusChangeListener",
					OnFocusChangeListener.class).invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnFocusChangeListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
		if (this.impl == null) {
			registerCall("addOnLayoutChangeListener",
					new Class<?>[] { OnLayoutChangeListener.class }, listener);
			return;
		}

		try {
			getMethodInParents("addOnLayoutChangeListener",
					OnLayoutChangeListener.class).invoke(this.impl, listener);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'addOnLayoutChangeListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
		if (this.impl == null) {
			registerCall("removeOnLayoutChangeListener",
					new Class<?>[] { OnLayoutChangeListener.class }, listener);
			return;
		}

		try {
			getMethodInParents("removeOnLayoutChangeListener",
					OnLayoutChangeListener.class).invoke(this.impl, listener);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'removeOnLayoutChangeListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void addOnAttachStateChangeListener(
			OnAttachStateChangeListener listener) {
		if (this.impl == null) {
			registerCall("addOnAttachStateChangeListener",
					new Class<?>[] { OnAttachStateChangeListener.class },
					listener);
			return;
		}

		try {
			getMethodInParents("addOnAttachStateChangeListener",
					OnAttachStateChangeListener.class).invoke(this.impl,
					listener);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'addOnAttachStateChangeListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void removeOnAttachStateChangeListener(
			OnAttachStateChangeListener listener) {
		if (this.impl == null) {
			registerCall("removeOnAttachStateChangeListener",
					new Class<?>[] { OnAttachStateChangeListener.class },
					listener);
			return;
		}

		try {
			getMethodInParents("removeOnAttachStateChangeListener",
					OnAttachStateChangeListener.class).invoke(this.impl,
					listener);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'removeOnAttachStateChangeListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnClickListener(OnClickListener l) {
		if (this.impl == null) {
			registerCall("setOnClickListener",
					new Class<?>[] { OnClickListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnClickListener", OnClickListener.class)
					.invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnClickListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnLongClickListener(OnLongClickListener l) {
		if (this.impl == null) {
			registerCall("setOnLongClickListener",
					new Class<?>[] { OnLongClickListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnLongClickListener",
					OnLongClickListener.class).invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnLongClickListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
		if (this.impl == null) {
			registerCall("setOnCreateContextMenuListener",
					new Class<?>[] { OnCreateContextMenuListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnCreateContextMenuListener",
					OnCreateContextMenuListener.class).invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnCreateContextMenuListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnKeyListener(OnKeyListener l) {
		if (this.impl == null) {
			registerCall("setOnKeyListener",
					new Class<?>[] { OnKeyListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnKeyListener", OnKeyListener.class).invoke(
					this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnKeyListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnTouchListener(OnTouchListener l) {
		if (this.impl == null) {
			registerCall("setOnTouchListener",
					new Class<?>[] { OnTouchListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnTouchListener", OnTouchListener.class)
					.invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnTouchListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnGenericMotionListener(OnGenericMotionListener l) {
		if (this.impl == null) {
			registerCall("setOnGenericMotionListener",
					new Class<?>[] { OnGenericMotionListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnGenericMotionListener",
					OnGenericMotionListener.class).invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnGenericMotionListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnHoverListener(OnHoverListener l) {
		if (this.impl == null) {
			registerCall("setOnHoverListener",
					new Class<?>[] { OnHoverListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnHoverListener", OnHoverListener.class)
					.invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnHoverListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnDragListener(OnDragListener l) {
		if (this.impl == null) {
			registerCall("setOnDragListener",
					new Class<?>[] { OnDragListener.class }, l);
			return;
		}

		try {
			getMethodInParents("setOnDragListener", OnDragListener.class)
					.invoke(this.impl, l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnDragListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void clearFocus() {
		if (this.impl == null) {
			registerCall("clearFocus", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("clearFocus").invoke(this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'clearFocus' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		if (this.impl == null) {
			registerCall("onFocusChanged", new Class<?>[] { Boolean.TYPE,
					Integer.TYPE, Rect.class }, gainFocus, direction,
					previouslyFocusedRect);
			return;
		}

		try {
			getMethodInParents("onFocusChanged", Boolean.TYPE, Integer.TYPE,
					Rect.class).invoke(this.impl, gainFocus, direction,
					previouslyFocusedRect);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onFocusChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void sendAccessibilityEvent(int eventType) {
		if (this.impl == null) {
			registerCall("sendAccessibilityEvent",
					new Class<?>[] { Integer.TYPE }, eventType);
			return;
		}

		try {
			getMethodInParents("sendAccessibilityEvent", Integer.TYPE).invoke(
					this.impl, eventType);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'sendAccessibilityEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void announceForAccessibility(CharSequence text) {
		if (this.impl == null) {
			registerCall("announceForAccessibility",
					new Class<?>[] { CharSequence.class }, text);
			return;
		}

		try {
			getMethodInParents("announceForAccessibility", CharSequence.class)
					.invoke(this.impl, text);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'announceForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
		if (this.impl == null) {
			registerCall("sendAccessibilityEventUnchecked",
					new Class<?>[] { AccessibilityEvent.class }, event);
			return;
		}

		try {
			getMethodInParents("sendAccessibilityEventUnchecked",
					AccessibilityEvent.class).invoke(this.impl, event);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'sendAccessibilityEventUnchecked' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
		if (this.impl == null) {
			registerCall("onPopulateAccessibilityEvent",
					new Class<?>[] { AccessibilityEvent.class }, event);
			return;
		}

		try {
			getMethodInParents("onPopulateAccessibilityEvent",
					AccessibilityEvent.class).invoke(this.impl, event);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onPopulateAccessibilityEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		if (this.impl == null) {
			registerCall("onInitializeAccessibilityEvent",
					new Class<?>[] { AccessibilityEvent.class }, event);
			return;
		}

		try {
			getMethodInParents("onInitializeAccessibilityEvent",
					AccessibilityEvent.class).invoke(this.impl, event);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onInitializeAccessibilityEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		if (this.impl == null) {
			registerCall("onInitializeAccessibilityNodeInfo",
					new Class<?>[] { AccessibilityNodeInfo.class }, info);
			return;
		}

		try {
			getMethodInParents("onInitializeAccessibilityNodeInfo",
					AccessibilityNodeInfo.class).invoke(this.impl, info);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onInitializeAccessibilityNodeInfo' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setAccessibilityDelegate(AccessibilityDelegate delegate) {
		if (this.impl == null) {
			registerCall("setAccessibilityDelegate",
					new Class<?>[] { AccessibilityDelegate.class }, delegate);
			return;
		}

		try {
			getMethodInParents("setAccessibilityDelegate",
					AccessibilityDelegate.class).invoke(this.impl, delegate);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setAccessibilityDelegate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setContentDescription(CharSequence contentDescription) {
		if (this.impl == null) {
			registerCall("setContentDescription",
					new Class<?>[] { CharSequence.class }, contentDescription);
			return;
		}

		try {
			getMethodInParents("setContentDescription", CharSequence.class)
					.invoke(this.impl, contentDescription);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setContentDescription' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onFocusLost() {
		if (this.impl == null) {
			registerCall("onFocusLost", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onFocusLost")
					.invoke(this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onFocusLost' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollContainer(boolean isScrollContainer) {
		if (this.impl == null) {
			registerCall("setScrollContainer", new Class<?>[] { Boolean.TYPE },
					isScrollContainer);
			return;
		}

		try {
			getMethodInParents("setScrollContainer", Boolean.TYPE).invoke(
					this.impl, isScrollContainer);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollContainer' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setDrawingCacheQuality(int quality) {
		if (this.impl == null) {
			registerCall("setDrawingCacheQuality",
					new Class<?>[] { Integer.TYPE }, quality);
			return;
		}

		try {
			getMethodInParents("setDrawingCacheQuality", Integer.TYPE).invoke(
					this.impl, quality);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setDrawingCacheQuality' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setKeepScreenOn(boolean keepScreenOn) {
		if (this.impl == null) {
			registerCall("setKeepScreenOn", new Class<?>[] { Boolean.TYPE },
					keepScreenOn);
			return;
		}

		try {
			getMethodInParents("setKeepScreenOn", Boolean.TYPE).invoke(
					this.impl, keepScreenOn);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setKeepScreenOn' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setNextFocusLeftId(int nextFocusLeftId) {
		if (this.impl == null) {
			registerCall("setNextFocusLeftId", new Class<?>[] { Integer.TYPE },
					nextFocusLeftId);
			return;
		}

		try {
			getMethodInParents("setNextFocusLeftId", Integer.TYPE).invoke(
					this.impl, nextFocusLeftId);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setNextFocusLeftId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setNextFocusRightId(int nextFocusRightId) {
		if (this.impl == null) {
			registerCall("setNextFocusRightId",
					new Class<?>[] { Integer.TYPE }, nextFocusRightId);
			return;
		}

		try {
			getMethodInParents("setNextFocusRightId", Integer.TYPE).invoke(
					this.impl, nextFocusRightId);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setNextFocusRightId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setNextFocusUpId(int nextFocusUpId) {
		if (this.impl == null) {
			registerCall("setNextFocusUpId", new Class<?>[] { Integer.TYPE },
					nextFocusUpId);
			return;
		}

		try {
			getMethodInParents("setNextFocusUpId", Integer.TYPE).invoke(
					this.impl, nextFocusUpId);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setNextFocusUpId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setNextFocusDownId(int nextFocusDownId) {
		if (this.impl == null) {
			registerCall("setNextFocusDownId", new Class<?>[] { Integer.TYPE },
					nextFocusDownId);
			return;
		}

		try {
			getMethodInParents("setNextFocusDownId", Integer.TYPE).invoke(
					this.impl, nextFocusDownId);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setNextFocusDownId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setNextFocusForwardId(int nextFocusForwardId) {
		if (this.impl == null) {
			registerCall("setNextFocusForwardId",
					new Class<?>[] { Integer.TYPE }, nextFocusForwardId);
			return;
		}

		try {
			getMethodInParents("setNextFocusForwardId", Integer.TYPE).invoke(
					this.impl, nextFocusForwardId);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setNextFocusForwardId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setFitsSystemWindows(boolean fitSystemWindows) {
		if (this.impl == null) {
			registerCall("setFitsSystemWindows",
					new Class<?>[] { Boolean.TYPE }, fitSystemWindows);
			return;
		}

		try {
			getMethodInParents("setFitsSystemWindows", Boolean.TYPE).invoke(
					this.impl, fitSystemWindows);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setFitsSystemWindows' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void requestFitSystemWindows() {
		if (this.impl == null) {
			registerCall("requestFitSystemWindows", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("requestFitSystemWindows").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'requestFitSystemWindows' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void makeOptionalFitsSystemWindows() {
		if (this.impl == null) {
			registerCall("makeOptionalFitsSystemWindows", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("makeOptionalFitsSystemWindows").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'makeOptionalFitsSystemWindows' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setVisibility(int visibility) {
		if (this.impl == null) {
			registerCall("setVisibility", new Class<?>[] { Integer.TYPE },
					visibility);
			return;
		}

		try {
			getMethodInParents("setVisibility", Integer.TYPE).invoke(this.impl,
					visibility);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setVisibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setEnabled(boolean enabled) {
		if (this.impl == null) {
			registerCall("setEnabled", new Class<?>[] { Boolean.TYPE }, enabled);
			return;
		}

		try {
			getMethodInParents("setEnabled", Boolean.TYPE).invoke(this.impl,
					enabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setFocusable(boolean focusable) {
		if (this.impl == null) {
			registerCall("setFocusable", new Class<?>[] { Boolean.TYPE },
					focusable);
			return;
		}

		try {
			getMethodInParents("setFocusable", Boolean.TYPE).invoke(this.impl,
					focusable);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setFocusable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setFocusableInTouchMode(boolean focusableInTouchMode) {
		if (this.impl == null) {
			registerCall("setFocusableInTouchMode",
					new Class<?>[] { Boolean.TYPE }, focusableInTouchMode);
			return;
		}

		try {
			getMethodInParents("setFocusableInTouchMode", Boolean.TYPE).invoke(
					this.impl, focusableInTouchMode);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setFocusableInTouchMode' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
		if (this.impl == null) {
			registerCall("setSoundEffectsEnabled",
					new Class<?>[] { Boolean.TYPE }, soundEffectsEnabled);
			return;
		}

		try {
			getMethodInParents("setSoundEffectsEnabled", Boolean.TYPE).invoke(
					this.impl, soundEffectsEnabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setSoundEffectsEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
		if (this.impl == null) {
			registerCall("setHapticFeedbackEnabled",
					new Class<?>[] { Boolean.TYPE }, hapticFeedbackEnabled);
			return;
		}

		try {
			getMethodInParents("setHapticFeedbackEnabled", Boolean.TYPE)
					.invoke(this.impl, hapticFeedbackEnabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setHapticFeedbackEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setLayoutDirection(int layoutDirection) {
		if (this.impl == null) {
			registerCall("setLayoutDirection", new Class<?>[] { Integer.TYPE },
					layoutDirection);
			return;
		}

		try {
			getMethodInParents("setLayoutDirection", Integer.TYPE).invoke(
					this.impl, layoutDirection);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setLayoutDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setHasTransientState(boolean hasTransientState) {
		if (this.impl == null) {
			registerCall("setHasTransientState",
					new Class<?>[] { Boolean.TYPE }, hasTransientState);
			return;
		}

		try {
			getMethodInParents("setHasTransientState", Boolean.TYPE).invoke(
					this.impl, hasTransientState);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setHasTransientState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setWillNotDraw(boolean willNotDraw) {
		if (this.impl == null) {
			registerCall("setWillNotDraw", new Class<?>[] { Boolean.TYPE },
					willNotDraw);
			return;
		}

		try {
			getMethodInParents("setWillNotDraw", Boolean.TYPE).invoke(
					this.impl, willNotDraw);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setWillNotDraw' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
		if (this.impl == null) {
			registerCall("setWillNotCacheDrawing",
					new Class<?>[] { Boolean.TYPE }, willNotCacheDrawing);
			return;
		}

		try {
			getMethodInParents("setWillNotCacheDrawing", Boolean.TYPE).invoke(
					this.impl, willNotCacheDrawing);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setWillNotCacheDrawing' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setClickable(boolean clickable) {
		if (this.impl == null) {
			registerCall("setClickable", new Class<?>[] { Boolean.TYPE },
					clickable);
			return;
		}

		try {
			getMethodInParents("setClickable", Boolean.TYPE).invoke(this.impl,
					clickable);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setClickable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setLongClickable(boolean longClickable) {
		if (this.impl == null) {
			registerCall("setLongClickable", new Class<?>[] { Boolean.TYPE },
					longClickable);
			return;
		}

		try {
			getMethodInParents("setLongClickable", Boolean.TYPE).invoke(
					this.impl, longClickable);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setLongClickable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setPressed(boolean pressed) {
		if (this.impl == null) {
			registerCall("setPressed", new Class<?>[] { Boolean.TYPE }, pressed);
			return;
		}

		try {
			getMethodInParents("setPressed", Boolean.TYPE).invoke(this.impl,
					pressed);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setPressed' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void dispatchSetPressed(boolean pressed) {
		if (this.impl == null) {
			registerCall("dispatchSetPressed", new Class<?>[] { Boolean.TYPE },
					pressed);
			return;
		}

		try {
			getMethodInParents("dispatchSetPressed", Boolean.TYPE).invoke(
					this.impl, pressed);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchSetPressed' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setSaveEnabled(boolean enabled) {
		if (this.impl == null) {
			registerCall("setSaveEnabled", new Class<?>[] { Boolean.TYPE },
					enabled);
			return;
		}

		try {
			getMethodInParents("setSaveEnabled", Boolean.TYPE).invoke(
					this.impl, enabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setSaveEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setFilterTouchesWhenObscured(boolean enabled) {
		if (this.impl == null) {
			registerCall("setFilterTouchesWhenObscured",
					new Class<?>[] { Boolean.TYPE }, enabled);
			return;
		}

		try {
			getMethodInParents("setFilterTouchesWhenObscured", Boolean.TYPE)
					.invoke(this.impl, enabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setFilterTouchesWhenObscured' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setSaveFromParentEnabled(boolean enabled) {
		if (this.impl == null) {
			registerCall("setSaveFromParentEnabled",
					new Class<?>[] { Boolean.TYPE }, enabled);
			return;
		}

		try {
			getMethodInParents("setSaveFromParentEnabled", Boolean.TYPE)
					.invoke(this.impl, enabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setSaveFromParentEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void clearAccessibilityFocus() {
		if (this.impl == null) {
			registerCall("clearAccessibilityFocus", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("clearAccessibilityFocus").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'clearAccessibilityFocus' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setImportantForAccessibility(int mode) {
		if (this.impl == null) {
			registerCall("setImportantForAccessibility",
					new Class<?>[] { Integer.TYPE }, mode);
			return;
		}

		try {
			getMethodInParents("setImportantForAccessibility", Integer.TYPE)
					.invoke(this.impl, mode);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setImportantForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setAccessibilityFocusable(int mode) {
		if (this.impl == null) {
			registerCall("setAccessibilityFocusable",
					new Class<?>[] { Integer.TYPE }, mode);
			return;
		}

		try {
			getMethodInParents("setAccessibilityFocusable", Integer.TYPE)
					.invoke(this.impl, mode);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setAccessibilityFocusable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void notifyAccessibilityStateChanged() {
		if (this.impl == null) {
			registerCall("notifyAccessibilityStateChanged", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("notifyAccessibilityStateChanged").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'notifyAccessibilityStateChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resetAccessibilityStateChanged() {
		if (this.impl == null) {
			registerCall("resetAccessibilityStateChanged", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resetAccessibilityStateChanged").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resetAccessibilityStateChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setAccessibilityCursorPosition(int position) {
		if (this.impl == null) {
			registerCall("setAccessibilityCursorPosition",
					new Class<?>[] { Integer.TYPE }, position);
			return;
		}

		try {
			getMethodInParents("setAccessibilityCursorPosition", Integer.TYPE)
					.invoke(this.impl, position);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setAccessibilityCursorPosition' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchStartTemporaryDetach() {
		if (this.impl == null) {
			registerCall("dispatchStartTemporaryDetach", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("dispatchStartTemporaryDetach").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchStartTemporaryDetach' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onStartTemporaryDetach() {
		if (this.impl == null) {
			registerCall("onStartTemporaryDetach", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onStartTemporaryDetach").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onStartTemporaryDetach' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchFinishTemporaryDetach() {
		if (this.impl == null) {
			registerCall("dispatchFinishTemporaryDetach", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("dispatchFinishTemporaryDetach").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchFinishTemporaryDetach' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onFinishTemporaryDetach() {
		if (this.impl == null) {
			registerCall("onFinishTemporaryDetach", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onFinishTemporaryDetach").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onFinishTemporaryDetach' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchWindowFocusChanged(boolean hasFocus) {
		if (this.impl == null) {
			registerCall("dispatchWindowFocusChanged",
					new Class<?>[] { Boolean.TYPE }, hasFocus);
			return;
		}

		try {
			getMethodInParents("dispatchWindowFocusChanged", Boolean.TYPE)
					.invoke(this.impl, hasFocus);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchWindowFocusChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if (this.impl == null) {
			registerCall("onWindowFocusChanged",
					new Class<?>[] { Boolean.TYPE }, hasWindowFocus);
			return;
		}

		try {
			getMethodInParents("onWindowFocusChanged", Boolean.TYPE).invoke(
					this.impl, hasWindowFocus);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onWindowFocusChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void dispatchVisibilityChanged(View changedView, int visibility) {
		if (this.impl == null) {
			registerCall("dispatchVisibilityChanged", new Class<?>[] {
					View.class, Integer.TYPE }, changedView, visibility);
			return;
		}

		try {
			getMethodInParents("dispatchVisibilityChanged", View.class,
					Integer.TYPE).invoke(this.impl, changedView, visibility);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchVisibilityChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onVisibilityChanged(View changedView, int visibility) {
		if (this.impl == null) {
			registerCall("onVisibilityChanged", new Class<?>[] { View.class,
					Integer.TYPE }, changedView, visibility);
			return;
		}

		try {
			getMethodInParents("onVisibilityChanged", View.class, Integer.TYPE)
					.invoke(this.impl, changedView, visibility);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onVisibilityChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchDisplayHint(int hint) {
		if (this.impl == null) {
			registerCall("dispatchDisplayHint",
					new Class<?>[] { Integer.TYPE }, hint);
			return;
		}

		try {
			getMethodInParents("dispatchDisplayHint", Integer.TYPE).invoke(
					this.impl, hint);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchDisplayHint' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onDisplayHint(int hint) {
		if (this.impl == null) {
			registerCall("onDisplayHint", new Class<?>[] { Integer.TYPE }, hint);
			return;
		}

		try {
			getMethodInParents("onDisplayHint", Integer.TYPE).invoke(this.impl,
					hint);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onDisplayHint' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchWindowVisibilityChanged(int visibility) {
		if (this.impl == null) {
			registerCall("dispatchWindowVisibilityChanged",
					new Class<?>[] { Integer.TYPE }, visibility);
			return;
		}

		try {
			getMethodInParents("dispatchWindowVisibilityChanged", Integer.TYPE)
					.invoke(this.impl, visibility);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchWindowVisibilityChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onWindowVisibilityChanged(int visibility) {
		if (this.impl == null) {
			registerCall("onWindowVisibilityChanged",
					new Class<?>[] { Integer.TYPE }, visibility);
			return;
		}

		try {
			getMethodInParents("onWindowVisibilityChanged", Integer.TYPE)
					.invoke(this.impl, visibility);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onWindowVisibilityChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void getWindowVisibleDisplayFrame(Rect outRect) {
		if (this.impl == null) {
			registerCall("getWindowVisibleDisplayFrame",
					new Class<?>[] { Rect.class }, outRect);
			return;
		}

		try {
			getMethodInParents("getWindowVisibleDisplayFrame", Rect.class)
					.invoke(this.impl, outRect);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getWindowVisibleDisplayFrame' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchConfigurationChanged(Configuration newConfig) {
		if (this.impl == null) {
			registerCall("dispatchConfigurationChanged",
					new Class<?>[] { Configuration.class }, newConfig);
			return;
		}

		try {
			getMethodInParents("dispatchConfigurationChanged",
					Configuration.class).invoke(this.impl, newConfig);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchConfigurationChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onConfigurationChanged(Configuration newConfig) {
		if (this.impl == null) {
			registerCall("onConfigurationChanged",
					new Class<?>[] { Configuration.class }, newConfig);
			return;
		}

		try {
			getMethodInParents("onConfigurationChanged", Configuration.class)
					.invoke(this.impl, newConfig);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onConfigurationChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void createContextMenu(ContextMenu menu) {
		if (this.impl == null) {
			registerCall("createContextMenu",
					new Class<?>[] { ContextMenu.class }, menu);
			return;
		}

		try {
			getMethodInParents("createContextMenu", ContextMenu.class).invoke(
					this.impl, menu);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'createContextMenu' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onCreateContextMenu(ContextMenu menu) {
		if (this.impl == null) {
			registerCall("onCreateContextMenu",
					new Class<?>[] { ContextMenu.class }, menu);
			return;
		}

		try {
			getMethodInParents("onCreateContextMenu", ContextMenu.class)
					.invoke(this.impl, menu);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onCreateContextMenu' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setHovered(boolean hovered) {
		if (this.impl == null) {
			registerCall("setHovered", new Class<?>[] { Boolean.TYPE }, hovered);
			return;
		}

		try {
			getMethodInParents("setHovered", Boolean.TYPE).invoke(this.impl,
					hovered);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setHovered' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onHoverChanged(boolean hovered) {
		if (this.impl == null) {
			registerCall("onHoverChanged", new Class<?>[] { Boolean.TYPE },
					hovered);
			return;
		}

		try {
			getMethodInParents("onHoverChanged", Boolean.TYPE).invoke(
					this.impl, hovered);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onHoverChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void cancelLongPress() {
		if (this.impl == null) {
			registerCall("cancelLongPress", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("cancelLongPress").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'cancelLongPress' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setTouchDelegate(TouchDelegate delegate) {
		if (this.impl == null) {
			registerCall("setTouchDelegate",
					new Class<?>[] { TouchDelegate.class }, delegate);
			return;
		}

		try {
			getMethodInParents("setTouchDelegate", TouchDelegate.class).invoke(
					this.impl, delegate);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setTouchDelegate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void bringToFront() {
		if (this.impl == null) {
			registerCall("bringToFront", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("bringToFront").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'bringToFront' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (this.impl == null) {
			registerCall("onScrollChanged", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE }, l, t, oldl,
					oldt);
			return;
		}

		try {
			getMethodInParents("onScrollChanged", Integer.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE).invoke(this.impl, l, t, oldl,
					oldt);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onScrollChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (this.impl == null) {
			registerCall("onSizeChanged", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE }, w, h, oldw,
					oldh);
			return;
		}

		try {
			getMethodInParents("onSizeChanged", Integer.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE).invoke(this.impl, w, h, oldw,
					oldh);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onSizeChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void dispatchDraw(Canvas canvas) {
		if (this.impl == null) {
			registerCall("dispatchDraw", new Class<?>[] { Canvas.class },
					canvas);
			return;
		}

		try {
			getMethodInParents("dispatchDraw", Canvas.class).invoke(this.impl,
					canvas);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchDraw' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollX(int value) {
		if (this.impl == null) {
			registerCall("setScrollX", new Class<?>[] { Integer.TYPE }, value);
			return;
		}

		try {
			getMethodInParents("setScrollX", Integer.TYPE).invoke(this.impl,
					value);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollY(int value) {
		if (this.impl == null) {
			registerCall("setScrollY", new Class<?>[] { Integer.TYPE }, value);
			return;
		}

		try {
			getMethodInParents("setScrollY", Integer.TYPE).invoke(this.impl,
					value);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void getDrawingRect(Rect outRect) {
		if (this.impl == null) {
			registerCall("getDrawingRect", new Class<?>[] { Rect.class },
					outRect);
			return;
		}

		try {
			getMethodInParents("getDrawingRect", Rect.class).invoke(this.impl,
					outRect);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getDrawingRect' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void getFocusRect(Rect r) {
		if (this.impl == null) {
			registerCall("getFocusRect", new Class<?>[] { Rect.class }, r);
			return;
		}

		try {
			getMethodInParents("getFocusRect", Rect.class).invoke(this.impl, r);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getFocusRect' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setCameraDistance(float distance) {
		if (this.impl == null) {
			registerCall("setCameraDistance", new Class<?>[] { float.class },
					distance);
			return;
		}

		try {
			getMethodInParents("setCameraDistance", float.class).invoke(
					this.impl, distance);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setCameraDistance' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setRotation(float rotation) {
		if (this.impl == null) {
			registerCall("setRotation", new Class<?>[] { float.class },
					rotation);
			return;
		}

		try {
			getMethodInParents("setRotation", float.class).invoke(this.impl,
					rotation);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setRotation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setRotationY(float rotationY) {
		if (this.impl == null) {
			registerCall("setRotationY", new Class<?>[] { float.class },
					rotationY);
			return;
		}

		try {
			getMethodInParents("setRotationY", float.class).invoke(this.impl,
					rotationY);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setRotationY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setRotationX(float rotationX) {
		if (this.impl == null) {
			registerCall("setRotationX", new Class<?>[] { float.class },
					rotationX);
			return;
		}

		try {
			getMethodInParents("setRotationX", float.class).invoke(this.impl,
					rotationX);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setRotationX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScaleX(float scaleX) {
		if (this.impl == null) {
			registerCall("setScaleX", new Class<?>[] { float.class }, scaleX);
			return;
		}

		try {
			getMethodInParents("setScaleX", float.class).invoke(this.impl,
					scaleX);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScaleX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScaleY(float scaleY) {
		if (this.impl == null) {
			registerCall("setScaleY", new Class<?>[] { float.class }, scaleY);
			return;
		}

		try {
			getMethodInParents("setScaleY", float.class).invoke(this.impl,
					scaleY);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScaleY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setPivotX(float pivotX) {
		if (this.impl == null) {
			registerCall("setPivotX", new Class<?>[] { float.class }, pivotX);
			return;
		}

		try {
			getMethodInParents("setPivotX", float.class).invoke(this.impl,
					pivotX);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setPivotX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setPivotY(float pivotY) {
		if (this.impl == null) {
			registerCall("setPivotY", new Class<?>[] { float.class }, pivotY);
			return;
		}

		try {
			getMethodInParents("setPivotY", float.class).invoke(this.impl,
					pivotY);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setPivotY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setAlpha(float alpha) {
		if (this.impl == null) {
			registerCall("setAlpha", new Class<?>[] { float.class }, alpha);
			return;
		}

		try {
			getMethodInParents("setAlpha", float.class)
					.invoke(this.impl, alpha);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setAlpha' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setX(float x) {
		if (this.impl == null) {
			registerCall("setX", new Class<?>[] { float.class }, x);
			return;
		}

		try {
			getMethodInParents("setX", float.class).invoke(this.impl, x);
		} catch (Exception e) {
			throw new ProxyException("Error executing method 'setX' of class '"
					+ this.impl.getClass().getName() + "'. Message:"
					+ e.getMessage(), e);
		}
	}

	public void setY(float y) {
		if (this.impl == null) {
			registerCall("setY", new Class<?>[] { float.class }, y);
			return;
		}

		try {
			getMethodInParents("setY", float.class).invoke(this.impl, y);
		} catch (Exception e) {
			throw new ProxyException("Error executing method 'setY' of class '"
					+ this.impl.getClass().getName() + "'. Message:"
					+ e.getMessage(), e);
		}
	}

	public void setTranslationX(float translationX) {
		if (this.impl == null) {
			registerCall("setTranslationX", new Class<?>[] { float.class },
					translationX);
			return;
		}

		try {
			getMethodInParents("setTranslationX", float.class).invoke(
					this.impl, translationX);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setTranslationX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setTranslationY(float translationY) {
		if (this.impl == null) {
			registerCall("setTranslationY", new Class<?>[] { float.class },
					translationY);
			return;
		}

		try {
			getMethodInParents("setTranslationY", float.class).invoke(
					this.impl, translationY);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setTranslationY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void getHitRect(Rect outRect) {
		if (this.impl == null) {
			registerCall("getHitRect", new Class<?>[] { Rect.class }, outRect);
			return;
		}

		try {
			getMethodInParents("getHitRect", Rect.class).invoke(this.impl,
					outRect);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getHitRect' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void getFocusedRect(Rect r) {
		if (this.impl == null) {
			registerCall("getFocusedRect", new Class<?>[] { Rect.class }, r);
			return;
		}

		try {
			getMethodInParents("getFocusedRect", Rect.class).invoke(this.impl,
					r);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getFocusedRect' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void offsetTopAndBottom(int offset) {
		if (this.impl == null) {
			registerCall("offsetTopAndBottom", new Class<?>[] { Integer.TYPE },
					offset);
			return;
		}

		try {
			getMethodInParents("offsetTopAndBottom", Integer.TYPE).invoke(
					this.impl, offset);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'offsetTopAndBottom' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void offsetLeftAndRight(int offset) {
		if (this.impl == null) {
			registerCall("offsetLeftAndRight", new Class<?>[] { Integer.TYPE },
					offset);
			return;
		}

		try {
			getMethodInParents("offsetLeftAndRight", Integer.TYPE).invoke(
					this.impl, offset);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'offsetLeftAndRight' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setLayoutParams(ViewGroup.LayoutParams params) {
		if (this.impl == null) {
			registerCall("setLayoutParams",
					new Class<?>[] { ViewGroup.LayoutParams.class }, params);
			return;
		}

		try {
			getMethodInParents("setLayoutParams", ViewGroup.LayoutParams.class)
					.invoke(this.impl, params);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setLayoutParams' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void scrollTo(int x, int y) {
		if (this.impl == null) {
			registerCall("scrollTo", new Class<?>[] { Integer.TYPE,
					Integer.TYPE }, x, y);
			return;
		}

		try {
			getMethodInParents("scrollTo", Integer.TYPE, Integer.TYPE).invoke(
					this.impl, x, y);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'scrollTo' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void scrollBy(int x, int y) {
		if (this.impl == null) {
			registerCall("scrollBy", new Class<?>[] { Integer.TYPE,
					Integer.TYPE }, x, y);
			return;
		}

		try {
			getMethodInParents("scrollBy", Integer.TYPE, Integer.TYPE).invoke(
					this.impl, x, y);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'scrollBy' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void invalidate(Rect dirty) {
		if (this.impl == null) {
			registerCall("invalidate", new Class<?>[] { Rect.class }, dirty);
			return;
		}

		try {
			getMethodInParents("invalidate", Rect.class).invoke(this.impl,
					dirty);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'invalidate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void invalidate(int l, int t, int r, int b) {
		if (this.impl == null) {
			registerCall("invalidate", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE }, l, t, r, b);
			return;
		}

		try {
			getMethodInParents("invalidate", Integer.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE).invoke(this.impl, l, t, r, b);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'invalidate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void invalidate() {
		if (this.impl == null) {
			registerCall("invalidate", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("invalidate").invoke(this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'invalidate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void invalidateParentCaches() {
		if (this.impl == null) {
			registerCall("invalidateParentCaches", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("invalidateParentCaches").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'invalidateParentCaches' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void invalidateParentIfNeeded() {
		if (this.impl == null) {
			registerCall("invalidateParentIfNeeded", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("invalidateParentIfNeeded").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'invalidateParentIfNeeded' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void computeOpaqueFlags() {
		if (this.impl == null) {
			registerCall("computeOpaqueFlags", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("computeOpaqueFlags").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeOpaqueFlags' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void postOnAnimation(Runnable action) {
		if (this.impl == null) {
			registerCall("postOnAnimation", new Class<?>[] { Runnable.class },
					action);
			return;
		}

		try {
			getMethodInParents("postOnAnimation", Runnable.class).invoke(
					this.impl, action);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postOnAnimation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void postOnAnimationDelayed(Runnable action, long delayMillis) {
		if (this.impl == null) {
			registerCall("postOnAnimationDelayed", new Class<?>[] {
					Runnable.class, long.class }, action, delayMillis);
			return;
		}

		try {
			getMethodInParents("postOnAnimationDelayed", Runnable.class,
					long.class).invoke(this.impl, action, delayMillis);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postOnAnimationDelayed' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void postInvalidate() {
		if (this.impl == null) {
			registerCall("postInvalidate", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("postInvalidate").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postInvalidate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void postInvalidate(int left, int top, int right, int bottom) {
		if (this.impl == null) {
			registerCall("postInvalidate", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE }, left, top,
					right, bottom);
			return;
		}

		try {
			getMethodInParents("postInvalidate", Integer.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE).invoke(this.impl, left, top,
					right, bottom);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postInvalidate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void postInvalidateDelayed(long delayMilliseconds) {
		if (this.impl == null) {
			registerCall("postInvalidateDelayed",
					new Class<?>[] { long.class }, delayMilliseconds);
			return;
		}

		try {
			getMethodInParents("postInvalidateDelayed", long.class).invoke(
					this.impl, delayMilliseconds);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postInvalidateDelayed' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void postInvalidateOnAnimation() {
		if (this.impl == null) {
			registerCall("postInvalidateOnAnimation", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("postInvalidateOnAnimation").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postInvalidateOnAnimation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void postInvalidateOnAnimation(int left, int top, int right,
			int bottom) {
		if (this.impl == null) {
			registerCall("postInvalidateOnAnimation", new Class<?>[] {
					Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE },
					left, top, right, bottom);
			return;
		}

		try {
			getMethodInParents("postInvalidateOnAnimation", Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(this.impl,
					left, top, right, bottom);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postInvalidateOnAnimation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void computeScroll() {
		if (this.impl == null) {
			registerCall("computeScroll", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("computeScroll").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeScroll' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setHorizontalFadingEdgeEnabled(
			boolean horizontalFadingEdgeEnabled) {
		if (this.impl == null) {
			registerCall("setHorizontalFadingEdgeEnabled",
					new Class<?>[] { Boolean.TYPE },
					horizontalFadingEdgeEnabled);
			return;
		}

		try {
			getMethodInParents("setHorizontalFadingEdgeEnabled", Boolean.TYPE)
					.invoke(this.impl, horizontalFadingEdgeEnabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setHorizontalFadingEdgeEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) {
		if (this.impl == null) {
			registerCall("setVerticalFadingEdgeEnabled",
					new Class<?>[] { Boolean.TYPE }, verticalFadingEdgeEnabled);
			return;
		}

		try {
			getMethodInParents("setVerticalFadingEdgeEnabled", Boolean.TYPE)
					.invoke(this.impl, verticalFadingEdgeEnabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setVerticalFadingEdgeEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
		if (this.impl == null) {
			registerCall("setHorizontalScrollBarEnabled",
					new Class<?>[] { Boolean.TYPE }, horizontalScrollBarEnabled);
			return;
		}

		try {
			getMethodInParents("setHorizontalScrollBarEnabled", Boolean.TYPE)
					.invoke(this.impl, horizontalScrollBarEnabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setHorizontalScrollBarEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
		if (this.impl == null) {
			registerCall("setVerticalScrollBarEnabled",
					new Class<?>[] { Boolean.TYPE }, verticalScrollBarEnabled);
			return;
		}

		try {
			getMethodInParents("setVerticalScrollBarEnabled", Boolean.TYPE)
					.invoke(this.impl, verticalScrollBarEnabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setVerticalScrollBarEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void recomputePadding() {
		if (this.impl == null) {
			registerCall("recomputePadding", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("recomputePadding").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'recomputePadding' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
		if (this.impl == null) {
			registerCall("setScrollbarFadingEnabled",
					new Class<?>[] { Boolean.TYPE }, fadeScrollbars);
			return;
		}

		try {
			getMethodInParents("setScrollbarFadingEnabled", Boolean.TYPE)
					.invoke(this.impl, fadeScrollbars);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollbarFadingEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollBarDefaultDelayBeforeFade(
			int scrollBarDefaultDelayBeforeFade) {
		if (this.impl == null) {
			registerCall("setScrollBarDefaultDelayBeforeFade",
					new Class<?>[] { Integer.TYPE },
					scrollBarDefaultDelayBeforeFade);
			return;
		}

		try {
			getMethodInParents("setScrollBarDefaultDelayBeforeFade",
					Integer.TYPE).invoke(this.impl,
					scrollBarDefaultDelayBeforeFade);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollBarDefaultDelayBeforeFade' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollBarFadeDuration(int scrollBarFadeDuration) {
		if (this.impl == null) {
			registerCall("setScrollBarFadeDuration",
					new Class<?>[] { Integer.TYPE }, scrollBarFadeDuration);
			return;
		}

		try {
			getMethodInParents("setScrollBarFadeDuration", Integer.TYPE)
					.invoke(this.impl, scrollBarFadeDuration);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollBarFadeDuration' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollBarSize(int scrollBarSize) {
		if (this.impl == null) {
			registerCall("setScrollBarSize", new Class<?>[] { Integer.TYPE },
					scrollBarSize);
			return;
		}

		try {
			getMethodInParents("setScrollBarSize", Integer.TYPE).invoke(
					this.impl, scrollBarSize);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollBarSize' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setScrollBarStyle(int style) {
		if (this.impl == null) {
			registerCall("setScrollBarStyle", new Class<?>[] { Integer.TYPE },
					style);
			return;
		}

		try {
			getMethodInParents("setScrollBarStyle", Integer.TYPE).invoke(
					this.impl, style);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setScrollBarStyle' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	/**
	 * Render the text
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	// @Override
	// protected void onDraw(Canvas canvas) {
	// super.onDraw(canvas);
	// canvas.drawText(mText, getPaddingLeft(), getPaddingTop() - mAscent,
	// mTextPaint);
	// }

	protected void onDraw(Canvas canvas) {
		if (this.impl == null) {
			registerCall("onDraw", new Class<?>[] { Canvas.class }, canvas);
			return;
		}

		try {
			getMethodInParents("onDraw", Canvas.class)
					.invoke(this.impl, canvas);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onDraw' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onAttachedToWindow() {
		if (this.impl == null) {
			registerCall("onAttachedToWindow", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onAttachedToWindow").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onAttachedToWindow' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onScreenStateChanged(int screenState) {
		if (this.impl == null) {
			registerCall("onScreenStateChanged",
					new Class<?>[] { Integer.TYPE }, screenState);
			return;
		}

		try {
			getMethodInParents("onScreenStateChanged", Integer.TYPE).invoke(
					this.impl, screenState);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onScreenStateChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resolveLayoutDirection() {
		if (this.impl == null) {
			registerCall("resolveLayoutDirection", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resolveLayoutDirection").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resolveLayoutDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onResolvedLayoutDirectionChanged() {
		if (this.impl == null) {
			registerCall("onResolvedLayoutDirectionChanged", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onResolvedLayoutDirectionChanged").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onResolvedLayoutDirectionChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resolvePadding() {
		if (this.impl == null) {
			registerCall("resolvePadding", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resolvePadding").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resolvePadding' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onPaddingChanged(int layoutDirection) {
		if (this.impl == null) {
			registerCall("onPaddingChanged", new Class<?>[] { Integer.TYPE },
					layoutDirection);
			return;
		}

		try {
			getMethodInParents("onPaddingChanged", Integer.TYPE).invoke(
					this.impl, layoutDirection);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onPaddingChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resetResolvedLayoutDirection() {
		if (this.impl == null) {
			registerCall("resetResolvedLayoutDirection", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resetResolvedLayoutDirection").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resetResolvedLayoutDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onResolvedLayoutDirectionReset() {
		if (this.impl == null) {
			registerCall("onResolvedLayoutDirectionReset", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onResolvedLayoutDirectionReset").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onResolvedLayoutDirectionReset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onDetachedFromWindow() {
		if (this.impl == null) {
			registerCall("onDetachedFromWindow", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onDetachedFromWindow").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onDetachedFromWindow' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onRestoreInstanceState(Parcelable state) {
		if (this.impl == null) {
			registerCall("onRestoreInstanceState",
					new Class<?>[] { Parcelable.class }, state);
			super.onRestoreInstanceState(state);
			return;
		}

		try {
			getMethodInParents("onRestoreInstanceState", Parcelable.class)
					.invoke(this.impl, state);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onRestoreInstanceState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setDuplicateParentStateEnabled(boolean enabled) {
		if (this.impl == null) {
			registerCall("setDuplicateParentStateEnabled",
					new Class<?>[] { Boolean.TYPE }, enabled);
			return;
		}

		try {
			getMethodInParents("setDuplicateParentStateEnabled", Boolean.TYPE)
					.invoke(this.impl, enabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setDuplicateParentStateEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setLayerType(int layerType, Paint paint) {
		if (this.impl == null) {
			registerCall("setLayerType", new Class<?>[] { Integer.TYPE,
					Paint.class }, layerType, paint);
			return;
		}

		try {
			getMethodInParents("setLayerType", Integer.TYPE, Paint.class)
					.invoke(this.impl, layerType, paint);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setLayerType' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void buildLayer() {
		if (this.impl == null) {
			registerCall("buildLayer", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("buildLayer").invoke(this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'buildLayer' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void destroyHardwareResources() {
		if (this.impl == null) {
			registerCall("destroyHardwareResources", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("destroyHardwareResources").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'destroyHardwareResources' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setDrawingCacheEnabled(boolean enabled) {
		if (this.impl == null) {
			registerCall("setDrawingCacheEnabled",
					new Class<?>[] { Boolean.TYPE }, enabled);
			return;
		}

		try {
			getMethodInParents("setDrawingCacheEnabled", Boolean.TYPE).invoke(
					this.impl, enabled);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setDrawingCacheEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void outputDirtyFlags(String indent, boolean clear, int clearMask) {
		if (this.impl == null) {
			registerCall("outputDirtyFlags", new Class<?>[] { String.class,
					Boolean.TYPE, Integer.TYPE }, indent, clear, clearMask);
			return;
		}

		try {
			getMethodInParents("outputDirtyFlags", String.class, Boolean.TYPE,
					Integer.TYPE).invoke(this.impl, indent, clear, clearMask);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'outputDirtyFlags' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void dispatchGetDisplayList() {
		if (this.impl == null) {
			registerCall("dispatchGetDisplayList", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("dispatchGetDisplayList").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchGetDisplayList' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void destroyDrawingCache() {
		if (this.impl == null) {
			registerCall("destroyDrawingCache", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("destroyDrawingCache").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'destroyDrawingCache' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setDrawingCacheBackgroundColor(int color) {
		if (this.impl == null) {
			registerCall("setDrawingCacheBackgroundColor",
					new Class<?>[] { Integer.TYPE }, color);
			return;
		}

		try {
			getMethodInParents("setDrawingCacheBackgroundColor", Integer.TYPE)
					.invoke(this.impl, color);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setDrawingCacheBackgroundColor' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void buildDrawingCache() {
		if (this.impl == null) {
			registerCall("buildDrawingCache", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("buildDrawingCache").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'buildDrawingCache' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void buildDrawingCache(boolean autoScale) {
		if (this.impl == null) {
			registerCall("buildDrawingCache", new Class<?>[] { Boolean.TYPE },
					autoScale);
			return;
		}

		try {
			getMethodInParents("buildDrawingCache", Boolean.TYPE).invoke(
					this.impl, autoScale);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'buildDrawingCache' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void draw(Canvas canvas) {
		if (this.impl == null) {
			registerCall("draw", new Class<?>[] { Canvas.class }, canvas);
			return;
		}

		try {
			getMethodInParents("draw", Canvas.class).invoke(this.impl, canvas);
		} catch (Exception e) {
			throw new ProxyException("Error executing method 'draw' of class '"
					+ this.impl.getClass().getName() + "'. Message:"
					+ e.getMessage(), e);
		}
	}

	public void layout(int l, int t, int r, int b) {
		if (this.impl == null) {
			registerCall("layout", new Class<?>[] { Integer.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE }, l, t, r, b);
			return;
		}

		try {
			getMethodInParents("layout", Integer.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE).invoke(this.impl, l, t, r, b);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'layout' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (this.impl == null) {
			registerCall("onLayout", new Class<?>[] { Boolean.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE },
					changed, left, top, right, bottom);
			return;
		}

		try {
			getMethodInParents("onLayout", Boolean.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(this.impl,
					changed, left, top, right, bottom);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onLayout' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onFinishInflate() {
		if (this.impl == null) {
			registerCall("onFinishInflate", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onFinishInflate").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onFinishInflate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void invalidateDrawable(Drawable drawable) {
		if (this.impl == null) {
			registerCall("invalidateDrawable",
					new Class<?>[] { Drawable.class }, drawable);
			return;
		}

		try {
			getMethodInParents("invalidateDrawable", Drawable.class).invoke(
					this.impl, drawable);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'invalidateDrawable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void scheduleDrawable(Drawable who, Runnable what, long when) {
		if (this.impl == null) {
			registerCall("scheduleDrawable", new Class<?>[] { Drawable.class,
					Runnable.class, long.class }, who, what, when);
			return;
		}

		try {
			getMethodInParents("scheduleDrawable", Drawable.class,
					Runnable.class, long.class).invoke(this.impl, who, what,
					when);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'scheduleDrawable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void unscheduleDrawable(Drawable who, Runnable what) {
		if (this.impl == null) {
			registerCall("unscheduleDrawable", new Class<?>[] { Drawable.class,
					Runnable.class }, who, what);
			return;
		}

		try {
			getMethodInParents("unscheduleDrawable", Drawable.class,
					Runnable.class).invoke(this.impl, who, what);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'unscheduleDrawable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void unscheduleDrawable(Drawable who) {
		if (this.impl == null) {
			registerCall("unscheduleDrawable",
					new Class<?>[] { Drawable.class }, who);
			return;
		}

		try {
			getMethodInParents("unscheduleDrawable", Drawable.class).invoke(
					this.impl, who);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'unscheduleDrawable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void drawableStateChanged() {
		if (this.impl == null) {
			registerCall("drawableStateChanged", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("drawableStateChanged").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'drawableStateChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void refreshDrawableState() {
		if (this.impl == null) {
			registerCall("refreshDrawableState", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("refreshDrawableState").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'refreshDrawableState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void jumpDrawablesToCurrentState() {
		if (this.impl == null) {
			registerCall("jumpDrawablesToCurrentState", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("jumpDrawablesToCurrentState").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'jumpDrawablesToCurrentState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setBackgroundColor(int color) {
		if (this.impl == null) {
			registerCall("setBackgroundColor", new Class<?>[] { Integer.TYPE },
					color);
			return;
		}

		try {
			getMethodInParents("setBackgroundColor", Integer.TYPE).invoke(
					this.impl, color);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setBackgroundColor' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setBackgroundResource(int resid) {
		if (this.impl == null) {
			registerCall("setBackgroundResource",
					new Class<?>[] { Integer.TYPE }, resid);
			return;
		}

		try {
			getMethodInParents("setBackgroundResource", Integer.TYPE).invoke(
					this.impl, resid);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setBackgroundResource' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setBackground(Drawable background) {
		if (this.impl == null) {
			registerCall("setBackground", new Class<?>[] { Drawable.class },
					background);
			return;
		}

		try {
			getMethodInParents("setBackground", Drawable.class).invoke(
					this.impl, background);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setBackground' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setBackgroundDrawable(Drawable background) {
		if (this.impl == null) {
			registerCall("setBackgroundDrawable",
					new Class<?>[] { Drawable.class }, background);
			return;
		}

		try {
			getMethodInParents("setBackgroundDrawable", Drawable.class).invoke(
					this.impl, background);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setBackgroundDrawable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setPadding(int left, int top, int right, int bottom) {
		if (this.impl == null) {
			registerCall("setPadding", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE }, left, top,
					right, bottom);
			return;
		}

		try {
			getMethodInParents("setPadding", Integer.TYPE, Integer.TYPE,
					Integer.TYPE, Integer.TYPE).invoke(this.impl, left, top,
					right, bottom);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setPadding' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setPaddingRelative(int start, int top, int end, int bottom) {
		if (this.impl == null) {
			registerCall("setPaddingRelative", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE }, start, top,
					end, bottom);
			return;
		}

		try {
			getMethodInParents("setPaddingRelative", Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(this.impl,
					start, top, end, bottom);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setPaddingRelative' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setLayoutInsets(Insets layoutInsets) {
		if (this.impl == null) {
			registerCall("setLayoutInsets", new Class<?>[] { Insets.class },
					layoutInsets);
			return;
		}

		try {
			getMethodInParents("setLayoutInsets", Insets.class).invoke(
					this.impl, layoutInsets);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setLayoutInsets' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setSelected(boolean selected) {
		if (this.impl == null) {
			registerCall("setSelected", new Class<?>[] { Boolean.TYPE },
					selected);
			return;
		}

		try {
			getMethodInParents("setSelected", Boolean.TYPE).invoke(this.impl,
					selected);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setSelected' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void dispatchSetSelected(boolean selected) {
		if (this.impl == null) {
			registerCall("dispatchSetSelected",
					new Class<?>[] { Boolean.TYPE }, selected);
			return;
		}

		try {
			getMethodInParents("dispatchSetSelected", Boolean.TYPE).invoke(
					this.impl, selected);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchSetSelected' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setActivated(boolean activated) {
		if (this.impl == null) {
			registerCall("setActivated", new Class<?>[] { Boolean.TYPE },
					activated);
			return;
		}

		try {
			getMethodInParents("setActivated", Boolean.TYPE).invoke(this.impl,
					activated);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setActivated' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void dispatchSetActivated(boolean activated) {
		if (this.impl == null) {
			registerCall("dispatchSetActivated",
					new Class<?>[] { Boolean.TYPE }, activated);
			return;
		}

		try {
			getMethodInParents("dispatchSetActivated", Boolean.TYPE).invoke(
					this.impl, activated);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchSetActivated' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void getLocationOnScreen(int[] location) {
		if (this.impl == null) {
			registerCall("getLocationOnScreen", new Class<?>[] { int[].class },
					location);
			return;
		}

		try {
			getMethodInParents("getLocationOnScreen", int[].class).invoke(
					this.impl, location);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getLocationOnScreen' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void getLocationInWindow(int[] location) {
		if (this.impl == null) {
			registerCall("getLocationInWindow", new Class<?>[] { int[].class },
					location);
			return;
		}

		try {
			getMethodInParents("getLocationInWindow", int[].class).invoke(
					this.impl, location);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getLocationInWindow' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setId(int id) {
		if (this.impl == null) {
			registerCall("setId", new Class<?>[] { Integer.TYPE }, id);
			return;
		}

		try {
			getMethodInParents("setId", Integer.TYPE).invoke(this.impl, id);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setIsRootNamespace(boolean isRoot) {
		if (this.impl == null) {
			registerCall("setIsRootNamespace", new Class<?>[] { Boolean.TYPE },
					isRoot);
			return;
		}

		try {
			getMethodInParents("setIsRootNamespace", Boolean.TYPE).invoke(
					this.impl, isRoot);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setIsRootNamespace' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setTag(final Object tag) {
		if (this.impl == null) {
			registerCall("setTag", new Class<?>[] { Object.class }, tag);
			return;
		}

		try {
			getMethodInParents("setTag", Object.class).invoke(this.impl, tag);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setTag' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setTagInternal(int key, Object tag) {
		if (this.impl == null) {
			registerCall("setTagInternal", new Class<?>[] { Integer.TYPE,
					Object.class }, key, tag);
			return;
		}

		try {
			getMethodInParents("setTagInternal", Integer.TYPE, Object.class)
					.invoke(this.impl, key, tag);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setTagInternal' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void debug() {
		if (this.impl == null) {
			registerCall("debug", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("debug").invoke(this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'debug' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void debug(int depth) {
		if (this.impl == null) {
			registerCall("debug", new Class<?>[] { Integer.TYPE }, depth);
			return;
		}

		try {
			getMethodInParents("debug", Integer.TYPE).invoke(this.impl, depth);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'debug' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void requestLayout() {
		if (this.impl == null) {
			registerCall("requestLayout", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("requestLayout").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'requestLayout' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void forceLayout() {
		if (this.impl == null) {
			registerCall("forceLayout", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("forceLayout")
					.invoke(this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'forceLayout' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (this.impl == null) {
			registerCall("onMeasure", new Class<?>[] { Integer.TYPE,
					Integer.TYPE }, widthMeasureSpec, heightMeasureSpec);
			return;
		}

		try {
			getMethodInParents("onMeasure", Integer.TYPE, Integer.TYPE).invoke(
					this.impl, widthMeasureSpec, heightMeasureSpec);
			setMeasuredDimension(measureWidth(widthMeasureSpec),
					measureHeight(heightMeasureSpec));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onMeasure' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// setMeasuredDimension(measureWidth(widthMeasureSpec),
	// measureHeight(heightMeasureSpec));
	// }

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	public void setMinimumHeight(int minHeight) {
		if (this.impl == null) {
			registerCall("setMinimumHeight", new Class<?>[] { Integer.TYPE },
					minHeight);
			return;
		}

		try {
			getMethodInParents("setMinimumHeight", Integer.TYPE).invoke(
					this.impl, minHeight);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setMinimumHeight' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setMinimumWidth(int minWidth) {
		if (this.impl == null) {
			registerCall("setMinimumWidth", new Class<?>[] { Integer.TYPE },
					minWidth);
			return;
		}

		try {
			getMethodInParents("setMinimumWidth", Integer.TYPE).invoke(
					this.impl, minWidth);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setMinimumWidth' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void startAnimation(Animation animation) {
		if (this.impl == null) {
			registerCall("startAnimation", new Class<?>[] { Animation.class },
					animation);
			return;
		}

		try {
			getMethodInParents("startAnimation", Animation.class).invoke(
					this.impl, animation);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'startAnimation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void clearAnimation() {
		if (this.impl == null) {
			registerCall("clearAnimation", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("clearAnimation").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'clearAnimation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setAnimation(Animation animation) {
		if (this.impl == null) {
			registerCall("setAnimation", new Class<?>[] { Animation.class },
					animation);
			return;
		}

		try {
			getMethodInParents("setAnimation", Animation.class).invoke(
					this.impl, animation);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setAnimation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onAnimationStart() {
		if (this.impl == null) {
			registerCall("onAnimationStart", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onAnimationStart").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onAnimationStart' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected void onAnimationEnd() {
		if (this.impl == null) {
			registerCall("onAnimationEnd", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onAnimationEnd").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onAnimationEnd' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void playSoundEffect(int soundConstant) {
		if (this.impl == null) {
			registerCall("playSoundEffect", new Class<?>[] { Integer.TYPE },
					soundConstant);
			return;
		}

		try {
			getMethodInParents("playSoundEffect", Integer.TYPE).invoke(
					this.impl, soundConstant);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'playSoundEffect' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setSystemUiVisibility(int visibility) {
		if (this.impl == null) {
			registerCall("setSystemUiVisibility",
					new Class<?>[] { Integer.TYPE }, visibility);
			return;
		}

		try {
			getMethodInParents("setSystemUiVisibility", Integer.TYPE).invoke(
					this.impl, visibility);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setSystemUiVisibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onWindowSystemUiVisibilityChanged(int visible) {
		if (this.impl == null) {
			registerCall("onWindowSystemUiVisibilityChanged",
					new Class<?>[] { Integer.TYPE }, visible);
			return;
		}

		try {
			getMethodInParents("onWindowSystemUiVisibilityChanged",
					Integer.TYPE).invoke(this.impl, visible);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onWindowSystemUiVisibilityChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchWindowSystemUiVisiblityChanged(int visible) {
		if (this.impl == null) {
			registerCall("dispatchWindowSystemUiVisiblityChanged",
					new Class<?>[] { Integer.TYPE }, visible);
			return;
		}

		try {
			getMethodInParents("dispatchWindowSystemUiVisiblityChanged",
					Integer.TYPE).invoke(this.impl, visible);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchWindowSystemUiVisiblityChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOnSystemUiVisibilityChangeListener(
			OnSystemUiVisibilityChangeListener l) {
		if (this.impl == null) {
			registerCall(
					"setOnSystemUiVisibilityChangeListener",
					new Class<?>[] { OnSystemUiVisibilityChangeListener.class },
					l);
			return;
		}

		try {
			getMethodInParents("setOnSystemUiVisibilityChangeListener",
					OnSystemUiVisibilityChangeListener.class).invoke(this.impl,
					l);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOnSystemUiVisibilityChangeListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void dispatchSystemUiVisibilityChanged(int visibility) {
		if (this.impl == null) {
			registerCall("dispatchSystemUiVisibilityChanged",
					new Class<?>[] { Integer.TYPE }, visibility);
			return;
		}

		try {
			getMethodInParents("dispatchSystemUiVisibilityChanged",
					Integer.TYPE).invoke(this.impl, visibility);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchSystemUiVisibilityChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setDisabledSystemUiVisibility(int flags) {
		if (this.impl == null) {
			registerCall("setDisabledSystemUiVisibility",
					new Class<?>[] { Integer.TYPE }, flags);
			return;
		}

		try {
			getMethodInParents("setDisabledSystemUiVisibility", Integer.TYPE)
					.invoke(this.impl, flags);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setDisabledSystemUiVisibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onCloseSystemDialogs(String reason) {
		if (this.impl == null) {
			registerCall("onCloseSystemDialogs",
					new Class<?>[] { String.class }, reason);
			return;
		}

		try {
			getMethodInParents("onCloseSystemDialogs", String.class).invoke(
					this.impl, reason);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onCloseSystemDialogs' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void applyDrawableToTransparentRegion(Drawable dr, Region region) {
		if (this.impl == null) {
			registerCall("applyDrawableToTransparentRegion", new Class<?>[] {
					Drawable.class, Region.class }, dr, region);
			return;
		}

		try {
			getMethodInParents("applyDrawableToTransparentRegion",
					Drawable.class, Region.class).invoke(this.impl, dr, region);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'applyDrawableToTransparentRegion' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setOverScrollMode(int overScrollMode) {
		if (this.impl == null) {
			registerCall("setOverScrollMode", new Class<?>[] { Integer.TYPE },
					overScrollMode);
			return;
		}

		try {
			getMethodInParents("setOverScrollMode", Integer.TYPE).invoke(
					this.impl, overScrollMode);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setOverScrollMode' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setTextDirection(int textDirection) {
		if (this.impl == null) {
			registerCall("setTextDirection", new Class<?>[] { Integer.TYPE },
					textDirection);
			return;
		}

		try {
			getMethodInParents("setTextDirection", Integer.TYPE).invoke(
					this.impl, textDirection);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setTextDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resolveTextDirection() {
		if (this.impl == null) {
			registerCall("resolveTextDirection", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resolveTextDirection").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resolveTextDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onResolvedTextDirectionChanged() {
		if (this.impl == null) {
			registerCall("onResolvedTextDirectionChanged", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onResolvedTextDirectionChanged").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onResolvedTextDirectionChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resetResolvedTextDirection() {
		if (this.impl == null) {
			registerCall("resetResolvedTextDirection", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resetResolvedTextDirection").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resetResolvedTextDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onResolvedTextDirectionReset() {
		if (this.impl == null) {
			registerCall("onResolvedTextDirectionReset", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onResolvedTextDirectionReset").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onResolvedTextDirectionReset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void setTextAlignment(int textAlignment) {
		if (this.impl == null) {
			registerCall("setTextAlignment", new Class<?>[] { Integer.TYPE },
					textAlignment);
			return;
		}

		try {
			getMethodInParents("setTextAlignment", Integer.TYPE).invoke(
					this.impl, textAlignment);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setTextAlignment' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resolveTextAlignment() {
		if (this.impl == null) {
			registerCall("resolveTextAlignment", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resolveTextAlignment").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resolveTextAlignment' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onResolvedTextAlignmentChanged() {
		if (this.impl == null) {
			registerCall("onResolvedTextAlignmentChanged", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onResolvedTextAlignmentChanged").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onResolvedTextAlignmentChanged' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void resetResolvedTextAlignment() {
		if (this.impl == null) {
			registerCall("resetResolvedTextAlignment", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("resetResolvedTextAlignment").invoke(this.impl,
					(Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'resetResolvedTextAlignment' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void onResolvedTextAlignmentReset() {
		if (this.impl == null) {
			registerCall("onResolvedTextAlignmentReset", new Class<?>[] {});
			return;
		}

		try {
			getMethodInParents("onResolvedTextAlignmentReset").invoke(
					this.impl, (Object[]) null);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onResolvedTextAlignmentReset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public void hackTurnOffWindowResizeAnim(boolean off) {
		if (this.impl == null) {
			registerCall("hackTurnOffWindowResizeAnim",
					new Class<?>[] { Boolean.TYPE }, off);
			return;
		}

		try {
			getMethodInParents("hackTurnOffWindowResizeAnim", Boolean.TYPE)
					.invoke(this.impl, off);
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hackTurnOffWindowResizeAnim' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getVerticalFadingEdgeLength() {
		if (this.impl == null) {
			registerCall("getVerticalFadingEdgeLength", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getVerticalFadingEdgeLength")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getVerticalFadingEdgeLength' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getHorizontalFadingEdgeLength() {
		if (this.impl == null) {
			registerCall("getHorizontalFadingEdgeLength", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getHorizontalFadingEdgeLength")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getHorizontalFadingEdgeLength' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getVerticalScrollbarWidth() {
		if (this.impl == null) {
			registerCall("getVerticalScrollbarWidth", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getVerticalScrollbarWidth")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getVerticalScrollbarWidth' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getHorizontalScrollbarHeight() {
		if (this.impl == null) {
			registerCall("getHorizontalScrollbarHeight", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getHorizontalScrollbarHeight")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getHorizontalScrollbarHeight' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getVerticalScrollbarPosition() {
		if (this.impl == null) {
			registerCall("getVerticalScrollbarPosition", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getVerticalScrollbarPosition")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getVerticalScrollbarPosition' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public OnFocusChangeListener getOnFocusChangeListener() {
		if (this.impl == null) {
			registerCall("getOnFocusChangeListener", new Class<?>[] {});

		}

		try {
			return (OnFocusChangeListener) (getMethodInParents("getOnFocusChangeListener")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getOnFocusChangeListener' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean hasOnClickListeners() {
		if (this.impl == null) {
			registerCall("hasOnClickListeners", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasOnClickListeners").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasOnClickListeners' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean performClick() {
		if (this.impl == null) {
			registerCall("performClick", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("performClick").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'performClick' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean callOnClick() {
		if (this.impl == null) {
			registerCall("callOnClick", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("callOnClick").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'callOnClick' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean performLongClick() {
		if (this.impl == null) {
			registerCall("performLongClick", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("performLongClick").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'performLongClick' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean performButtonActionOnTouchDown(MotionEvent event) {
		if (this.impl == null) {
			registerCall("performButtonActionOnTouchDown",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents(
					"performButtonActionOnTouchDown", MotionEvent.class)
					.invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'performButtonActionOnTouchDown' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean showContextMenu() {
		if (this.impl == null) {
			registerCall("showContextMenu", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("showContextMenu").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'showContextMenu' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean showContextMenu(float x, float y, int metaState) {
		if (this.impl == null) {
			registerCall("showContextMenu", new Class<?>[] { float.class,
					float.class, Integer.TYPE }, x, y, metaState);

		}

		try {
			return (Boolean) (getMethodInParents("showContextMenu",
					float.class, float.class, Integer.TYPE).invoke(this.impl,
					x, y, metaState));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'showContextMenu' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public ActionMode startActionMode(ActionMode.Callback callback) {
		if (this.impl == null) {
			registerCall("startActionMode",
					new Class<?>[] { ActionMode.Callback.class }, callback);

		}

		try {
			return (ActionMode) (getMethodInParents("startActionMode",
					ActionMode.Callback.class).invoke(this.impl, callback));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'startActionMode' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean requestRectangleOnScreen(Rect rectangle) {
		if (this.impl == null) {
			registerCall("requestRectangleOnScreen",
					new Class<?>[] { Rect.class }, rectangle);

		}

		try {
			return (Boolean) (getMethodInParents("requestRectangleOnScreen",
					Rect.class).invoke(this.impl, rectangle));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'requestRectangleOnScreen' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
		if (this.impl == null) {
			registerCall("requestRectangleOnScreen", new Class<?>[] {
					Rect.class, Boolean.TYPE }, rectangle, immediate);

		}

		try {
			return (Boolean) (getMethodInParents("requestRectangleOnScreen",
					Rect.class, Boolean.TYPE).invoke(this.impl, rectangle,
					immediate));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'requestRectangleOnScreen' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean hasFocus() {
		if (this.impl == null) {
			registerCall("hasFocus", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasFocus").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasFocus' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean hasFocusable() {
		if (this.impl == null) {
			registerCall("hasFocusable", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasFocusable").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasFocusable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		if (this.impl == null) {
			registerCall("dispatchPopulateAccessibilityEvent",
					new Class<?>[] { AccessibilityEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents(
					"dispatchPopulateAccessibilityEvent",
					AccessibilityEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchPopulateAccessibilityEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public AccessibilityNodeInfo createAccessibilityNodeInfo() {
		if (this.impl == null) {
			registerCall("createAccessibilityNodeInfo", new Class<?>[] {});

		}

		try {
			return (AccessibilityNodeInfo) (getMethodInParents("createAccessibilityNodeInfo")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'createAccessibilityNodeInfo' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getActualAndReportedWindowLeftDelta() {
		if (this.impl == null) {
			registerCall("getActualAndReportedWindowLeftDelta",
					new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getActualAndReportedWindowLeftDelta")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getActualAndReportedWindowLeftDelta' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getActualAndReportedWindowTopDelta() {
		if (this.impl == null) {
			registerCall("getActualAndReportedWindowTopDelta",
					new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getActualAndReportedWindowTopDelta")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getActualAndReportedWindowTopDelta' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean isVisibleToUser() {
		if (this.impl == null) {
			registerCall("isVisibleToUser", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isVisibleToUser").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isVisibleToUser' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean isVisibleToUser(Rect boundInView) {
		if (this.impl == null) {
			registerCall("isVisibleToUser", new Class<?>[] { Rect.class },
					boundInView);

		}

		try {
			return (Boolean) (getMethodInParents("isVisibleToUser", Rect.class)
					.invoke(this.impl, boundInView));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isVisibleToUser' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getAccessibilityViewId() {
		if (this.impl == null) {
			registerCall("getAccessibilityViewId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getAccessibilityViewId")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getAccessibilityViewId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getAccessibilityWindowId() {
		if (this.impl == null) {
			registerCall("getAccessibilityWindowId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getAccessibilityWindowId")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getAccessibilityWindowId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public CharSequence getContentDescription() {
		if (this.impl == null) {
			registerCall("getContentDescription", new Class<?>[] {});

		}

		try {
			return (CharSequence) (getMethodInParents("getContentDescription")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getContentDescription' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isFocused() {
		if (this.impl == null) {
			registerCall("isFocused", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isFocused").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isFocused' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public View findFocus() {
		if (this.impl == null) {
			registerCall("findFocus", new Class<?>[] {});

		}

		try {
			return (View) (getMethodInParents("findFocus").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'findFocus' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isScrollContainer() {
		if (this.impl == null) {
			registerCall("isScrollContainer", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isScrollContainer").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isScrollContainer' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getDrawingCacheQuality() {
		if (this.impl == null) {
			registerCall("getDrawingCacheQuality", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getDrawingCacheQuality")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getDrawingCacheQuality' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean getKeepScreenOn() {
		if (this.impl == null) {
			registerCall("getKeepScreenOn", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("getKeepScreenOn").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getKeepScreenOn' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getNextFocusLeftId() {
		if (this.impl == null) {
			registerCall("getNextFocusLeftId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getNextFocusLeftId").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getNextFocusLeftId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getNextFocusRightId() {
		if (this.impl == null) {
			registerCall("getNextFocusRightId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getNextFocusRightId").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getNextFocusRightId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getNextFocusUpId() {
		if (this.impl == null) {
			registerCall("getNextFocusUpId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getNextFocusUpId").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getNextFocusUpId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getNextFocusDownId() {
		if (this.impl == null) {
			registerCall("getNextFocusDownId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getNextFocusDownId").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getNextFocusDownId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getNextFocusForwardId() {
		if (this.impl == null) {
			registerCall("getNextFocusForwardId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getNextFocusForwardId")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getNextFocusForwardId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isShown() {
		if (this.impl == null) {
			registerCall("isShown", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isShown").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isShown' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean fitSystemWindows(Rect insets) {
		if (this.impl == null) {
			registerCall("fitSystemWindows", new Class<?>[] { Rect.class },
					insets);

		}

		try {
			return (Boolean) (getMethodInParents("fitSystemWindows", Rect.class)
					.invoke(this.impl, insets));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'fitSystemWindows' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean getFitsSystemWindows() {
		if (this.impl == null) {
			registerCall("getFitsSystemWindows", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("getFitsSystemWindows")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getFitsSystemWindows' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean fitsSystemWindows() {
		if (this.impl == null) {
			registerCall("fitsSystemWindows", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("fitsSystemWindows").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'fitsSystemWindows' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getVisibility() {
		if (this.impl == null) {
			registerCall("getVisibility", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getVisibility").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getVisibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isEnabled() {
		if (this.impl == null) {
			registerCall("isEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isEnabled").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isSoundEffectsEnabled() {
		if (this.impl == null) {
			registerCall("isSoundEffectsEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isSoundEffectsEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isSoundEffectsEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isHapticFeedbackEnabled() {
		if (this.impl == null) {
			registerCall("isHapticFeedbackEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isHapticFeedbackEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isHapticFeedbackEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getLayoutDirection() {
		if (this.impl == null) {
			registerCall("getLayoutDirection", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getLayoutDirection").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getLayoutDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getResolvedLayoutDirection() {
		if (this.impl == null) {
			registerCall("getResolvedLayoutDirection", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getResolvedLayoutDirection")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getResolvedLayoutDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isLayoutRtl() {
		if (this.impl == null) {
			registerCall("isLayoutRtl", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isLayoutRtl").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isLayoutRtl' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean hasTransientState() {
		if (this.impl == null) {
			registerCall("hasTransientState", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasTransientState").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasTransientState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean willNotDraw() {
		if (this.impl == null) {
			registerCall("willNotDraw", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("willNotDraw").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'willNotDraw' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean willNotCacheDrawing() {
		if (this.impl == null) {
			registerCall("willNotCacheDrawing", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("willNotCacheDrawing").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'willNotCacheDrawing' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isClickable() {
		if (this.impl == null) {
			registerCall("isClickable", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isClickable").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isClickable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isLongClickable() {
		if (this.impl == null) {
			registerCall("isLongClickable", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isLongClickable").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isLongClickable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isPressed() {
		if (this.impl == null) {
			registerCall("isPressed", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isPressed").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isPressed' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isSaveEnabled() {
		if (this.impl == null) {
			registerCall("isSaveEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isSaveEnabled").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isSaveEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean getFilterTouchesWhenObscured() {
		if (this.impl == null) {
			registerCall("getFilterTouchesWhenObscured", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("getFilterTouchesWhenObscured")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getFilterTouchesWhenObscured' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isSaveFromParentEnabled() {
		if (this.impl == null) {
			registerCall("isSaveFromParentEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isSaveFromParentEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isSaveFromParentEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public View focusSearch(int direction) {
		if (this.impl == null) {
			registerCall("focusSearch", new Class<?>[] { Integer.TYPE },
					direction);

		}

		try {
			return (View) (getMethodInParents("focusSearch", Integer.TYPE)
					.invoke(this.impl, direction));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'focusSearch' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (this.impl == null) {
			registerCall("dispatchUnhandledMove", new Class<?>[] { View.class,
					Integer.TYPE }, focused, direction);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchUnhandledMove",
					View.class, Integer.TYPE).invoke(this.impl, focused,
					direction));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchUnhandledMove' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean requestAccessibilityFocus() {
		if (this.impl == null) {
			registerCall("requestAccessibilityFocus", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("requestAccessibilityFocus")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'requestAccessibilityFocus' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
		if (this.impl == null) {
			registerCall("requestFocus", new Class<?>[] { Integer.TYPE,
					Rect.class }, direction, previouslyFocusedRect);

		}

		try {
			return (Boolean) (getMethodInParents("requestFocus", Integer.TYPE,
					Rect.class).invoke(this.impl, direction,
					previouslyFocusedRect));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'requestFocus' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getImportantForAccessibility() {
		if (this.impl == null) {
			registerCall("getImportantForAccessibility", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getImportantForAccessibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getImportantForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isImportantForAccessibility() {
		if (this.impl == null) {
			registerCall("isImportantForAccessibility", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isImportantForAccessibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isImportantForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getAccessibilityFocusable() {
		if (this.impl == null) {
			registerCall("getAccessibilityFocusable", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getAccessibilityFocusable")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getAccessibilityFocusable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isAccessibilityFocusable() {
		if (this.impl == null) {
			registerCall("isAccessibilityFocusable", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isAccessibilityFocusable")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isAccessibilityFocusable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public ViewParent getParentForAccessibility() {
		if (this.impl == null) {
			registerCall("getParentForAccessibility", new Class<?>[] {});

		}

		try {
			return (ViewParent) (getMethodInParents("getParentForAccessibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getParentForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean includeForAccessibility() {
		if (this.impl == null) {
			registerCall("includeForAccessibility", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("includeForAccessibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'includeForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isActionableForAccessibility() {
		if (this.impl == null) {
			registerCall("isActionableForAccessibility", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isActionableForAccessibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isActionableForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean performAccessibilityAction(int action, Bundle arguments) {
		if (this.impl == null) {
			registerCall("performAccessibilityAction", new Class<?>[] {
					Integer.TYPE, Bundle.class }, action, arguments);

		}

		try {
			return (Boolean) (getMethodInParents("performAccessibilityAction",
					Integer.TYPE, Bundle.class).invoke(this.impl, action,
					arguments));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'performAccessibilityAction' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public CharSequence getIterableTextForAccessibility() {
		if (this.impl == null) {
			registerCall("getIterableTextForAccessibility", new Class<?>[] {});

		}

		try {
			return (CharSequence) (getMethodInParents("getIterableTextForAccessibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getIterableTextForAccessibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getAccessibilityCursorPosition() {
		if (this.impl == null) {
			registerCall("getAccessibilityCursorPosition", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getAccessibilityCursorPosition")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getAccessibilityCursorPosition' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public KeyEvent.DispatcherState getKeyDispatcherState() {
		if (this.impl == null) {
			registerCall("getKeyDispatcherState", new Class<?>[] {});

		}

		try {
			return (KeyEvent.DispatcherState) (getMethodInParents("getKeyDispatcherState")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getKeyDispatcherState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if (this.impl == null) {
			registerCall("dispatchKeyEventPreIme",
					new Class<?>[] { KeyEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchKeyEventPreIme",
					KeyEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchKeyEventPreIme' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (this.impl == null) {
			registerCall("dispatchKeyEvent", new Class<?>[] { KeyEvent.class },
					event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchKeyEvent",
					KeyEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchKeyEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		if (this.impl == null) {
			registerCall("dispatchKeyShortcutEvent",
					new Class<?>[] { KeyEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchKeyShortcutEvent",
					KeyEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchKeyShortcutEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("dispatchTouchEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchTouchEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchTouchEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onFilterTouchEventForSecurity(MotionEvent event) {
		if (this.impl == null) {
			registerCall("onFilterTouchEventForSecurity",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents(
					"onFilterTouchEventForSecurity", MotionEvent.class).invoke(
					this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onFilterTouchEventForSecurity' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchTrackballEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("dispatchTrackballEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchTrackballEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchTrackballEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchGenericMotionEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("dispatchGenericMotionEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchGenericMotionEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchGenericMotionEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean dispatchHoverEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("dispatchHoverEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchHoverEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchHoverEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean hasHoveredChild() {
		if (this.impl == null) {
			registerCall("hasHoveredChild", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasHoveredChild").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasHoveredChild' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean dispatchGenericPointerEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("dispatchGenericPointerEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchGenericPointerEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchGenericPointerEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("dispatchGenericFocusedEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchGenericFocusedEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchGenericFocusedEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean hasWindowFocus() {
		if (this.impl == null) {
			registerCall("hasWindowFocus", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasWindowFocus").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasWindowFocus' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getWindowVisibility() {
		if (this.impl == null) {
			registerCall("getWindowVisibility", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getWindowVisibility").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getWindowVisibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isInTouchMode() {
		if (this.impl == null) {
			registerCall("isInTouchMode", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isInTouchMode").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isInTouchMode' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (this.impl == null) {
			registerCall("onKeyPreIme", new Class<?>[] { Integer.TYPE,
					KeyEvent.class }, keyCode, event);

		}

		try {
			return (Boolean) (getMethodInParents("onKeyPreIme", Integer.TYPE,
					KeyEvent.class).invoke(this.impl, keyCode, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onKeyPreIme' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (this.impl == null) {
			registerCall("onKeyDown", new Class<?>[] { Integer.TYPE,
					KeyEvent.class }, keyCode, event);

		}

		try {
			return (Boolean) (getMethodInParents("onKeyDown", Integer.TYPE,
					KeyEvent.class).invoke(this.impl, keyCode, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onKeyDown' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (this.impl == null) {
			registerCall("onKeyLongPress", new Class<?>[] { Integer.TYPE,
					KeyEvent.class }, keyCode, event);

		}

		try {
			return (Boolean) (getMethodInParents("onKeyLongPress",
					Integer.TYPE, KeyEvent.class).invoke(this.impl, keyCode,
					event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onKeyLongPress' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (this.impl == null) {
			registerCall("onKeyUp", new Class<?>[] { Integer.TYPE,
					KeyEvent.class }, keyCode, event);

		}

		try {
			return (Boolean) (getMethodInParents("onKeyUp", Integer.TYPE,
					KeyEvent.class).invoke(this.impl, keyCode, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onKeyUp' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		if (this.impl == null) {
			registerCall("onKeyMultiple", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, KeyEvent.class }, keyCode, repeatCount, event);

		}

		try {
			return (Boolean) (getMethodInParents("onKeyMultiple", Integer.TYPE,
					Integer.TYPE, KeyEvent.class).invoke(this.impl, keyCode,
					repeatCount, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onKeyMultiple' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onKeyShortcut(int keyCode, KeyEvent event) {
		if (this.impl == null) {
			registerCall("onKeyShortcut", new Class<?>[] { Integer.TYPE,
					KeyEvent.class }, keyCode, event);

		}

		try {
			return (Boolean) (getMethodInParents("onKeyShortcut", Integer.TYPE,
					KeyEvent.class).invoke(this.impl, keyCode, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onKeyShortcut' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onCheckIsTextEditor() {
		if (this.impl == null) {
			registerCall("onCheckIsTextEditor", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("onCheckIsTextEditor").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onCheckIsTextEditor' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		if (this.impl == null) {
			registerCall("onCreateInputConnection",
					new Class<?>[] { EditorInfo.class }, outAttrs);

		}

		try {
			return (InputConnection) (getMethodInParents(
					"onCreateInputConnection", EditorInfo.class).invoke(
					this.impl, outAttrs));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onCreateInputConnection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean checkInputConnectionProxy(View view) {
		if (this.impl == null) {
			registerCall("checkInputConnectionProxy",
					new Class<?>[] { View.class }, view);

		}

		try {
			return (Boolean) (getMethodInParents("checkInputConnectionProxy",
					View.class).invoke(this.impl, view));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'checkInputConnectionProxy' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected ContextMenuInfo getContextMenuInfo() {
		if (this.impl == null) {
			registerCall("getContextMenuInfo", new Class<?>[] {});

		}

		try {
			return (ContextMenuInfo) (getMethodInParents("getContextMenuInfo")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getContextMenuInfo' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onTrackballEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("onTrackballEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("onTrackballEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onTrackballEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onGenericMotionEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("onGenericMotionEvent",
					new Class<?>[] { MotionEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("onGenericMotionEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onGenericMotionEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onHoverEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("onHoverEvent", new Class<?>[] { MotionEvent.class },
					event);

		}

		try {
			return (Boolean) (getMethodInParents("onHoverEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onHoverEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isHovered() {
		if (this.impl == null) {
			registerCall("isHovered", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isHovered").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isHovered' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (this.impl == null) {
			registerCall("onTouchEvent", new Class<?>[] { MotionEvent.class },
					event);

		}

		try {
			return (Boolean) (getMethodInParents("onTouchEvent",
					MotionEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onTouchEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isInScrollingContainer() {
		if (this.impl == null) {
			registerCall("isInScrollingContainer", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isInScrollingContainer")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isInScrollingContainer' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public TouchDelegate getTouchDelegate() {
		if (this.impl == null) {
			registerCall("getTouchDelegate", new Class<?>[] {});

		}

		try {
			return (TouchDelegate) (getMethodInParents("getTouchDelegate")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTouchDelegate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Matrix getMatrix() {
		if (this.impl == null) {
			registerCall("getMatrix", new Class<?>[] {});

		}

		try {
			return (Matrix) (getMethodInParents("getMatrix").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getMatrix' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getCameraDistance() {
		if (this.impl == null) {
			registerCall("getCameraDistance", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getCameraDistance").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getCameraDistance' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getRotation() {
		if (this.impl == null) {
			registerCall("getRotation", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getRotation").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getRotation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getRotationY() {
		if (this.impl == null) {
			registerCall("getRotationY", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getRotationY").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getRotationY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getRotationX() {
		if (this.impl == null) {
			registerCall("getRotationX", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getRotationX").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getRotationX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getScaleX() {
		if (this.impl == null) {
			registerCall("getScaleX", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getScaleX").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getScaleX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getScaleY() {
		if (this.impl == null) {
			registerCall("getScaleY", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getScaleY").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getScaleY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getPivotX() {
		if (this.impl == null) {
			registerCall("getPivotX", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getPivotX").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPivotX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getPivotY() {
		if (this.impl == null) {
			registerCall("getPivotY", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getPivotY").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPivotY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getAlpha() {
		if (this.impl == null) {
			registerCall("getAlpha", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getAlpha").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getAlpha' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean hasOverlappingRendering() {
		if (this.impl == null) {
			registerCall("hasOverlappingRendering", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasOverlappingRendering")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasOverlappingRendering' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isDirty() {
		if (this.impl == null) {
			registerCall("isDirty", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isDirty").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isDirty' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getX() {
		if (this.impl == null) {
			registerCall("getX", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getX").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException("Error executing method 'getX' of class '"
					+ this.impl.getClass().getName() + "'. Message:"
					+ e.getMessage(), e);
		}
	}

	public float getY() {
		if (this.impl == null) {
			registerCall("getY", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getY").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException("Error executing method 'getY' of class '"
					+ this.impl.getClass().getName() + "'. Message:"
					+ e.getMessage(), e);
		}
	}

	public float getTranslationX() {
		if (this.impl == null) {
			registerCall("getTranslationX", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getTranslationX").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTranslationX' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public float getTranslationY() {
		if (this.impl == null) {
			registerCall("getTranslationY", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getTranslationY").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTranslationY' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
		if (this.impl == null) {
			registerCall("getGlobalVisibleRect", new Class<?>[] { Rect.class,
					Point.class }, r, globalOffset);

		}

		try {
			return (Boolean) (getMethodInParents("getGlobalVisibleRect",
					Rect.class, Point.class).invoke(this.impl, r, globalOffset));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getGlobalVisibleRect' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public ViewGroup.LayoutParams getLayoutParams() {
		if (this.impl == null) {
			registerCall("getLayoutParams", new Class<?>[] {});

		}

		try {
			return (ViewGroup.LayoutParams) (getMethodInParents("getLayoutParams")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getLayoutParams' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean awakenScrollBars() {
		if (this.impl == null) {
			registerCall("awakenScrollBars", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("awakenScrollBars").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'awakenScrollBars' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean awakenScrollBars(int startDelay) {
		if (this.impl == null) {
			registerCall("awakenScrollBars", new Class<?>[] { Integer.TYPE },
					startDelay);

		}

		try {
			return (Boolean) (getMethodInParents("awakenScrollBars",
					Integer.TYPE).invoke(this.impl, startDelay));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'awakenScrollBars' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
		if (this.impl == null) {
			registerCall("awakenScrollBars", new Class<?>[] { Integer.TYPE,
					Boolean.TYPE }, startDelay, invalidate);

		}

		try {
			return (Boolean) (getMethodInParents("awakenScrollBars",
					Integer.TYPE, Boolean.TYPE).invoke(this.impl, startDelay,
					invalidate));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'awakenScrollBars' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isOpaque() {
		if (this.impl == null) {
			registerCall("isOpaque", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isOpaque").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isOpaque' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean hasOpaqueScrollbars() {
		if (this.impl == null) {
			registerCall("hasOpaqueScrollbars", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("hasOpaqueScrollbars").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'hasOpaqueScrollbars' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Handler getHandler() {
		if (this.impl == null) {
			registerCall("getHandler", new Class<?>[] {});

		}

		try {
			return (Handler) (getMethodInParents("getHandler").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getHandler' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean post(Runnable action) {
		if (this.impl == null) {
			registerCall("post", new Class<?>[] { Runnable.class }, action);

		}

		try {
			return (Boolean) (getMethodInParents("post", Runnable.class)
					.invoke(this.impl, action));
		} catch (Exception e) {
			throw new ProxyException("Error executing method 'post' of class '"
					+ this.impl.getClass().getName() + "'. Message:"
					+ e.getMessage(), e);
		}
	}

	public boolean postDelayed(Runnable action, long delayMillis) {
		if (this.impl == null) {
			registerCall("postDelayed", new Class<?>[] { Runnable.class,
					long.class }, action, delayMillis);

		}

		try {
			return (Boolean) (getMethodInParents("postDelayed", Runnable.class,
					long.class).invoke(this.impl, action, delayMillis));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'postDelayed' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean removeCallbacks(Runnable action) {
		if (this.impl == null) {
			registerCall("removeCallbacks", new Class<?>[] { Runnable.class },
					action);

		}

		try {
			return (Boolean) (getMethodInParents("removeCallbacks",
					Runnable.class).invoke(this.impl, action));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'removeCallbacks' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isHorizontalFadingEdgeEnabled() {
		if (this.impl == null) {
			registerCall("isHorizontalFadingEdgeEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isHorizontalFadingEdgeEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isHorizontalFadingEdgeEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isVerticalFadingEdgeEnabled() {
		if (this.impl == null) {
			registerCall("isVerticalFadingEdgeEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isVerticalFadingEdgeEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isVerticalFadingEdgeEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected float getTopFadingEdgeStrength() {
		if (this.impl == null) {
			registerCall("getTopFadingEdgeStrength", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getTopFadingEdgeStrength")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTopFadingEdgeStrength' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected float getBottomFadingEdgeStrength() {
		if (this.impl == null) {
			registerCall("getBottomFadingEdgeStrength", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getBottomFadingEdgeStrength")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getBottomFadingEdgeStrength' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected float getLeftFadingEdgeStrength() {
		if (this.impl == null) {
			registerCall("getLeftFadingEdgeStrength", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getLeftFadingEdgeStrength")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getLeftFadingEdgeStrength' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected float getRightFadingEdgeStrength() {
		if (this.impl == null) {
			registerCall("getRightFadingEdgeStrength", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getRightFadingEdgeStrength")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getRightFadingEdgeStrength' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isHorizontalScrollBarEnabled() {
		if (this.impl == null) {
			registerCall("isHorizontalScrollBarEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isHorizontalScrollBarEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isHorizontalScrollBarEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isVerticalScrollBarEnabled() {
		if (this.impl == null) {
			registerCall("isVerticalScrollBarEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isVerticalScrollBarEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isVerticalScrollBarEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isScrollbarFadingEnabled() {
		if (this.impl == null) {
			registerCall("isScrollbarFadingEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isScrollbarFadingEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isScrollbarFadingEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getScrollBarDefaultDelayBeforeFade() {
		if (this.impl == null) {
			registerCall("getScrollBarDefaultDelayBeforeFade",
					new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getScrollBarDefaultDelayBeforeFade")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getScrollBarDefaultDelayBeforeFade' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getScrollBarFadeDuration() {
		if (this.impl == null) {
			registerCall("getScrollBarFadeDuration", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getScrollBarFadeDuration")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getScrollBarFadeDuration' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getScrollBarSize() {
		if (this.impl == null) {
			registerCall("getScrollBarSize", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getScrollBarSize").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getScrollBarSize' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getScrollBarStyle() {
		if (this.impl == null) {
			registerCall("getScrollBarStyle", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getScrollBarStyle").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getScrollBarStyle' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int computeHorizontalScrollRange() {
		if (this.impl == null) {
			registerCall("computeHorizontalScrollRange", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("computeHorizontalScrollRange")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeHorizontalScrollRange' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int computeHorizontalScrollOffset() {
		if (this.impl == null) {
			registerCall("computeHorizontalScrollOffset", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("computeHorizontalScrollOffset")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeHorizontalScrollOffset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int computeHorizontalScrollExtent() {
		if (this.impl == null) {
			registerCall("computeHorizontalScrollExtent", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("computeHorizontalScrollExtent")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeHorizontalScrollExtent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int computeVerticalScrollRange() {
		if (this.impl == null) {
			registerCall("computeVerticalScrollRange", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("computeVerticalScrollRange")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeVerticalScrollRange' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int computeVerticalScrollOffset() {
		if (this.impl == null) {
			registerCall("computeVerticalScrollOffset", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("computeVerticalScrollOffset")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeVerticalScrollOffset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int computeVerticalScrollExtent() {
		if (this.impl == null) {
			registerCall("computeVerticalScrollExtent", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("computeVerticalScrollExtent")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'computeVerticalScrollExtent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean canScrollHorizontally(int direction) {
		if (this.impl == null) {
			registerCall("canScrollHorizontally",
					new Class<?>[] { Integer.TYPE }, direction);

		}

		try {
			return (Boolean) (getMethodInParents("canScrollHorizontally",
					Integer.TYPE).invoke(this.impl, direction));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'canScrollHorizontally' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean canScrollVertically(int direction) {
		if (this.impl == null) {
			registerCall("canScrollVertically",
					new Class<?>[] { Integer.TYPE }, direction);

		}

		try {
			return (Boolean) (getMethodInParents("canScrollVertically",
					Integer.TYPE).invoke(this.impl, direction));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'canScrollVertically' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean isVerticalScrollBarHidden() {
		if (this.impl == null) {
			registerCall("isVerticalScrollBarHidden", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isVerticalScrollBarHidden")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isVerticalScrollBarHidden' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean canResolveLayoutDirection() {
		if (this.impl == null) {
			registerCall("canResolveLayoutDirection", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("canResolveLayoutDirection")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'canResolveLayoutDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getWindowAttachCount() {
		if (this.impl == null) {
			registerCall("getWindowAttachCount", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getWindowAttachCount")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getWindowAttachCount' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public IBinder getWindowToken() {
		if (this.impl == null) {
			registerCall("getWindowToken", new Class<?>[] {});

		}

		try {
			return (IBinder) (getMethodInParents("getWindowToken").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getWindowToken' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public IBinder getApplicationWindowToken() {
		if (this.impl == null) {
			registerCall("getApplicationWindowToken", new Class<?>[] {});

		}

		try {
			return (IBinder) (getMethodInParents("getApplicationWindowToken")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getApplicationWindowToken' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected Parcelable onSaveInstanceState() {
		super.onSaveInstanceState();
		if (this.impl == null) {
			registerCall("onSaveInstanceState", new Class<?>[] {});
		}

		try {
			return (Parcelable) (getMethodInParents("onSaveInstanceState")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onSaveInstanceState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public long getDrawingTime() {
		if (this.impl == null) {
			registerCall("getDrawingTime", new Class<?>[] {});

		}

		try {
			return (Long) (getMethodInParents("getDrawingTime").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getDrawingTime' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isDuplicateParentStateEnabled() {
		if (this.impl == null) {
			registerCall("isDuplicateParentStateEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isDuplicateParentStateEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isDuplicateParentStateEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getLayerType() {
		if (this.impl == null) {
			registerCall("getLayerType", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getLayerType").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getLayerType' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isDrawingCacheEnabled() {
		if (this.impl == null) {
			registerCall("isDrawingCacheEnabled", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isDrawingCacheEnabled")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isDrawingCacheEnabled' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean canHaveDisplayList() {
		if (this.impl == null) {
			registerCall("canHaveDisplayList", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("canHaveDisplayList").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'canHaveDisplayList' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Bitmap getDrawingCache() {
		if (this.impl == null) {
			registerCall("getDrawingCache", new Class<?>[] {});

		}

		try {
			return (Bitmap) (getMethodInParents("getDrawingCache").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getDrawingCache' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Bitmap getDrawingCache(boolean autoScale) {
		if (this.impl == null) {
			registerCall("getDrawingCache", new Class<?>[] { Boolean.TYPE },
					autoScale);

		}

		try {
			return (Bitmap) (getMethodInParents("getDrawingCache", Boolean.TYPE)
					.invoke(this.impl, autoScale));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getDrawingCache' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getDrawingCacheBackgroundColor() {
		if (this.impl == null) {
			registerCall("getDrawingCacheBackgroundColor", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getDrawingCacheBackgroundColor")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getDrawingCacheBackgroundColor' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isInEditMode() {
		if (this.impl == null) {
			registerCall("isInEditMode", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isInEditMode").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isInEditMode' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean isPaddingOffsetRequired() {
		if (this.impl == null) {
			registerCall("isPaddingOffsetRequired", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isPaddingOffsetRequired")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isPaddingOffsetRequired' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getLeftPaddingOffset() {
		if (this.impl == null) {
			registerCall("getLeftPaddingOffset", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getLeftPaddingOffset")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getLeftPaddingOffset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getRightPaddingOffset() {
		if (this.impl == null) {
			registerCall("getRightPaddingOffset", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getRightPaddingOffset")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getRightPaddingOffset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getTopPaddingOffset() {
		if (this.impl == null) {
			registerCall("getTopPaddingOffset", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getTopPaddingOffset").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTopPaddingOffset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getBottomPaddingOffset() {
		if (this.impl == null) {
			registerCall("getBottomPaddingOffset", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getBottomPaddingOffset")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getBottomPaddingOffset' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getFadeTop(boolean offsetRequired) {
		if (this.impl == null) {
			registerCall("getFadeTop", new Class<?>[] { Boolean.TYPE },
					offsetRequired);

		}

		try {
			return (Integer) (getMethodInParents("getFadeTop", Boolean.TYPE)
					.invoke(this.impl, offsetRequired));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getFadeTop' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getFadeHeight(boolean offsetRequired) {
		if (this.impl == null) {
			registerCall("getFadeHeight", new Class<?>[] { Boolean.TYPE },
					offsetRequired);

		}

		try {
			return (Integer) (getMethodInParents("getFadeHeight", Boolean.TYPE)
					.invoke(this.impl, offsetRequired));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getFadeHeight' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isHardwareAccelerated() {
		if (this.impl == null) {
			registerCall("isHardwareAccelerated", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isHardwareAccelerated")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isHardwareAccelerated' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getSolidColor() {
		if (this.impl == null) {
			registerCall("getSolidColor", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getSolidColor").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getSolidColor' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isLayoutRequested() {
		if (this.impl == null) {
			registerCall("isLayoutRequested", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isLayoutRequested").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isLayoutRequested' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean setFrame(int left, int top, int right, int bottom) {
		if (this.impl == null) {
			registerCall("setFrame", new Class<?>[] { Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE }, left, top,
					right, bottom);

		}

		try {
			return (Boolean) (getMethodInParents("setFrame", Integer.TYPE,
					Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke(this.impl,
					left, top, right, bottom));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'setFrame' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Resources getResources() {
		if (this.impl == null) {
			registerCall("getResources", new Class<?>[] {});

		}

		try {
			return (Resources) (getMethodInParents("getResources").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getResources' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getResolvedLayoutDirection(Drawable who) {
		if (this.impl == null) {
			registerCall("getResolvedLayoutDirection",
					new Class<?>[] { Drawable.class }, who);

		}

		try {
			return (Integer) (getMethodInParents("getResolvedLayoutDirection",
					Drawable.class).invoke(this.impl, who));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getResolvedLayoutDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean verifyDrawable(Drawable who) {
		if (this.impl == null) {
			registerCall("verifyDrawable", new Class<?>[] { Drawable.class },
					who);

		}

		try {
			return (Boolean) (getMethodInParents("verifyDrawable",
					Drawable.class).invoke(this.impl, who));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'verifyDrawable' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int[] onCreateDrawableState(int extraSpace) {
		if (this.impl == null) {
			registerCall("onCreateDrawableState",
					new Class<?>[] { Integer.TYPE }, extraSpace);

		}

		try {
			return (int[]) (getMethodInParents("onCreateDrawableState",
					Integer.TYPE).invoke(this.impl, extraSpace));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onCreateDrawableState' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Drawable getBackground() {
		if (this.impl == null) {
			registerCall("getBackground", new Class<?>[] {});

		}

		try {
			return (Drawable) (getMethodInParents("getBackground").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getBackground' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getPaddingTop() {
		if (this.impl == null) {
			registerCall("getPaddingTop", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getPaddingTop").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPaddingTop' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getPaddingBottom() {
		if (this.impl == null) {
			registerCall("getPaddingBottom", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getPaddingBottom").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPaddingBottom' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getPaddingLeft() {
		if (this.impl == null) {
			registerCall("getPaddingLeft", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getPaddingLeft").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPaddingLeft' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getPaddingStart() {
		if (this.impl == null) {
			registerCall("getPaddingStart", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getPaddingStart").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPaddingStart' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getPaddingRight() {
		if (this.impl == null) {
			registerCall("getPaddingRight", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getPaddingRight").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPaddingRight' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getPaddingEnd() {
		if (this.impl == null) {
			registerCall("getPaddingEnd", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getPaddingEnd").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getPaddingEnd' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isPaddingRelative() {
		if (this.impl == null) {
			registerCall("isPaddingRelative", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isPaddingRelative").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isPaddingRelative' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Insets getOpticalInsets() {
		if (this.impl == null) {
			registerCall("getOpticalInsets", new Class<?>[] {});

		}

		try {
			return (Insets) (getMethodInParents("getOpticalInsets").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getOpticalInsets' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isSelected() {
		if (this.impl == null) {
			registerCall("isSelected", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isSelected").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isSelected' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isActivated() {
		if (this.impl == null) {
			registerCall("isActivated", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isActivated").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isActivated' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public ViewTreeObserver getViewTreeObserver() {
		if (this.impl == null) {
			registerCall("getViewTreeObserver", new Class<?>[] {});

		}

		try {
			return (ViewTreeObserver) (getMethodInParents("getViewTreeObserver")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getViewTreeObserver' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public View getRootView() {
		if (this.impl == null) {
			registerCall("getRootView", new Class<?>[] {});

		}

		try {
			return (View) (getMethodInParents("getRootView").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getRootView' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected View findViewTraversal(int id) {
		if (this.impl == null) {
			registerCall("findViewTraversal", new Class<?>[] { Integer.TYPE },
					id);

		}

		try {
			return (View) (getMethodInParents("findViewTraversal", Integer.TYPE)
					.invoke(this.impl, id));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'findViewTraversal' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected View findViewWithTagTraversal(Object tag) {
		if (this.impl == null) {
			registerCall("findViewWithTagTraversal",
					new Class<?>[] { Object.class }, tag);

		}

		try {
			return (View) (getMethodInParents("findViewWithTagTraversal",
					Object.class).invoke(this.impl, tag));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'findViewWithTagTraversal' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean isRootNamespace() {
		if (this.impl == null) {
			registerCall("isRootNamespace", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("isRootNamespace").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'isRootNamespace' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getId() {
		if (this.impl == null) {
			registerCall("getId", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getId").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getId' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Object getTag() {
		if (this.impl == null) {
			registerCall("getTag", new Class<?>[] {});

		}

		try {
			return (Object) (getMethodInParents("getTag").invoke(this.impl,
					(Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTag' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Object getTag(int key) {
		if (this.impl == null) {
			registerCall("getTag", new Class<?>[] { Integer.TYPE }, key);

		}

		try {
			return (Object) (getMethodInParents("getTag", Integer.TYPE).invoke(
					this.impl, key));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTag' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getBaseline() {
		if (this.impl == null) {
			registerCall("getBaseline", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getBaseline").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getBaseline' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getSuggestedMinimumHeight() {
		if (this.impl == null) {
			registerCall("getSuggestedMinimumHeight", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getSuggestedMinimumHeight")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getSuggestedMinimumHeight' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected int getSuggestedMinimumWidth() {
		if (this.impl == null) {
			registerCall("getSuggestedMinimumWidth", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getSuggestedMinimumWidth")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getSuggestedMinimumWidth' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getMinimumHeight() {
		if (this.impl == null) {
			registerCall("getMinimumHeight", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getMinimumHeight").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getMinimumHeight' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getMinimumWidth() {
		if (this.impl == null) {
			registerCall("getMinimumWidth", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getMinimumWidth").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getMinimumWidth' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public Animation getAnimation() {
		if (this.impl == null) {
			registerCall("getAnimation", new Class<?>[] {});

		}

		try {
			return (Animation) (getMethodInParents("getAnimation").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getAnimation' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected boolean onSetAlpha(int alpha) {
		if (this.impl == null) {
			registerCall("onSetAlpha", new Class<?>[] { Integer.TYPE }, alpha);

		}

		try {
			return (Boolean) (getMethodInParents("onSetAlpha", Integer.TYPE)
					.invoke(this.impl, alpha));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onSetAlpha' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean gatherTransparentRegion(Region region) {
		if (this.impl == null) {
			registerCall("gatherTransparentRegion",
					new Class<?>[] { Region.class }, region);

		}

		try {
			return (Boolean) (getMethodInParents("gatherTransparentRegion",
					Region.class).invoke(this.impl, region));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'gatherTransparentRegion' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean performHapticFeedback(int feedbackConstant) {
		if (this.impl == null) {
			registerCall("performHapticFeedback",
					new Class<?>[] { Integer.TYPE }, feedbackConstant);

		}

		try {
			return (Boolean) (getMethodInParents("performHapticFeedback",
					Integer.TYPE).invoke(this.impl, feedbackConstant));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'performHapticFeedback' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean performHapticFeedback(int feedbackConstant, int flags) {
		if (this.impl == null) {
			registerCall("performHapticFeedback", new Class<?>[] {
					Integer.TYPE, Integer.TYPE }, feedbackConstant, flags);

		}

		try {
			return (Boolean) (getMethodInParents("performHapticFeedback",
					Integer.TYPE, Integer.TYPE).invoke(this.impl,
					feedbackConstant, flags));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'performHapticFeedback' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getSystemUiVisibility() {
		if (this.impl == null) {
			registerCall("getSystemUiVisibility", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getSystemUiVisibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getSystemUiVisibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getWindowSystemUiVisibility() {
		if (this.impl == null) {
			registerCall("getWindowSystemUiVisibility", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getWindowSystemUiVisibility")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getWindowSystemUiVisibility' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean onDragEvent(DragEvent event) {
		if (this.impl == null) {
			registerCall("onDragEvent", new Class<?>[] { DragEvent.class },
					event);

		}

		try {
			return (Boolean) (getMethodInParents("onDragEvent", DragEvent.class)
					.invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'onDragEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean dispatchDragEvent(DragEvent event) {
		if (this.impl == null) {
			registerCall("dispatchDragEvent",
					new Class<?>[] { DragEvent.class }, event);

		}

		try {
			return (Boolean) (getMethodInParents("dispatchDragEvent",
					DragEvent.class).invoke(this.impl, event));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'dispatchDragEvent' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getOverScrollMode() {
		if (this.impl == null) {
			registerCall("getOverScrollMode", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getOverScrollMode").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getOverScrollMode' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected float getVerticalScrollFactor() {
		if (this.impl == null) {
			registerCall("getVerticalScrollFactor", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getVerticalScrollFactor")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getVerticalScrollFactor' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	protected float getHorizontalScrollFactor() {
		if (this.impl == null) {
			registerCall("getHorizontalScrollFactor", new Class<?>[] {});

		}

		try {
			return (Float) (getMethodInParents("getHorizontalScrollFactor")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getHorizontalScrollFactor' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getTextDirection() {
		if (this.impl == null) {
			registerCall("getTextDirection", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getTextDirection").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTextDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getResolvedTextDirection() {
		if (this.impl == null) {
			registerCall("getResolvedTextDirection", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getResolvedTextDirection")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getResolvedTextDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean canResolveTextDirection() {
		if (this.impl == null) {
			registerCall("canResolveTextDirection", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("canResolveTextDirection")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'canResolveTextDirection' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getTextAlignment() {
		if (this.impl == null) {
			registerCall("getTextAlignment", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getTextAlignment").invoke(
					this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getTextAlignment' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public int getResolvedTextAlignment() {
		if (this.impl == null) {
			registerCall("getResolvedTextAlignment", new Class<?>[] {});

		}

		try {
			return (Integer) (getMethodInParents("getResolvedTextAlignment")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'getResolvedTextAlignment' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public boolean canResolveTextAlignment() {
		if (this.impl == null) {
			registerCall("canResolveTextAlignment", new Class<?>[] {});

		}

		try {
			return (Boolean) (getMethodInParents("canResolveTextAlignment")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'canResolveTextAlignment' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

	public ViewPropertyAnimator animate() {
		if (this.impl == null) {
			registerCall("animate", new Class<?>[] {});

		}

		try {
			return (ViewPropertyAnimator) (getMethodInParents("animate")
					.invoke(this.impl, (Object[]) null));
		} catch (Exception e) {
			throw new ProxyException(
					"Error executing method 'animate' of class '"
							+ this.impl.getClass().getName() + "'. Message:"
							+ e.getMessage(), e);
		}
	}

}
