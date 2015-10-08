package com.godic.d_ui.b_scan;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.godic.a_proc.AppSession;
import com.godic.b_util.AppUtils;
import com.godic.b_util.LineShaders;
import com.godic.c_data.a_scan.ScanData;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Obb2D;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vec2F;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Word;
import com.qualcomm.vuforia.WordResult;

public class ScanRender implements GLSurfaceView.Renderer {

	private static final String LOGTAG = "ScanRender";

	private AppSession vuforiaAppSession;

	private static final int MAX_NB_WORDS = 132;
	private static final float TEXTBOX_PADDING = 0.0f;
	private static final float ROIVertices[] = { -0.5f, -0.5f, 0.0f, 0.5f,
			-0.5f, 0.0f, 0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f };
	private static final int NUM_QUAD_OBJECT_INDICES = 8;
	private static final short ROIIndices[] = { 0, 1, 1, 2, 2, 3, 3, 0 };
	private static final float quadVertices[] = { -0.5f, -0.5f, 0.0f, 0.5f,
			-0.5f, 0.0f, 0.5f, 0.5f, 0.0f, -0.5f, 0.5f, 0.0f, };
	private static final short quadIndices[] = { 0, 1, 1, 2, 2, 3, 3, 0 };

	private ByteBuffer mROIVerts = null;
	private ByteBuffer mROIIndices = null;

	public boolean mIsActive = false;

	// Reference to main activity *
	public ScanActivity mActivity;
	private int shaderProgramID;
	private int vertexHandle;
	private int mvpMatrixHandle;
	private Renderer mRenderer;
	private int lineOpacityHandle;
	private int lineColorHandle;

	public float ROICenterX;
	public float ROICenterY;
	public float ROIWidth;
	public float ROIHeight;
	private int viewportPosition_x;
	private int viewportPosition_y;
	private int viewportSize_x;
	private int viewportSize_y;
	private ByteBuffer mQuadVerts;
	private ByteBuffer mQuadIndices;

	// ////////////////////////////
	private ArrayList<String> tempList = new ArrayList<String>();
	private ArrayList<String> words;
	private HashSet<String> wordset;
	private ScanData scanData = ScanData.getInstance();

	public ScanRender(ScanActivity activity, AppSession session) {
		mActivity = activity;
		vuforiaAppSession = session;
	}

	// Called when the surface is created or recreated.
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

		// Call function to initialize rendering:
		initRendering();

		// Call Vuforia function to (re)initialize rendering after first use
		// or after OpenGL ES context was lost (e.g. after onPause/onResume):
		vuforiaAppSession.onSurfaceCreated();
	}

	// Called when the surface changed size.
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

		mActivity.configureVideoBackgroundROI();

		// Call Vuforia function to handle render surface size changes:
		vuforiaAppSession.onSurfaceChanged(width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		renderFrame();
		// pileWordsReco();
		realtimeWordsReco();
		// /////////////////////////////////
		if (scanData.bitmapInfo.stateBitmap) {
			Log.e("onDrawFrame in", scanData.bitmapInfo.stateBitmap + "");
			createBitmap();
			scanData.bitmapInfo.stateBitmap = false;
			Log.e("onDrawFrame out", scanData.bitmapInfo.stateBitmap + "");
		}
		// ///////////////////////////////////
	}

	private void pileWordsReco() {
		ArrayList<String> words;
		synchronized (tempList) {
			words = new ArrayList<String>(tempList);
			tempList.clear();
		}

		for (String engword : words) {
			if (!scanData.wordList.contains(engword)) {
				mActivity.updateAddWordListUI(engword);
			}
		}
	}

	private void realtimeWordsReco() {

		synchronized (tempList) {
			words = new ArrayList<String>(tempList);
			// wordset = new HashSet(tempList);
			tempList.clear();
		}

		// for (String engword : wordset) {
		// if (!scanData.wordList.contains(engword)) {
		// mActivity.updateWordListUI(engword);
		// }
		// }

		for (String engword : words) {
			if (!scanData.wordList.contains(engword)) {
				scanData.wordList.add(engword);
				mActivity.updateAddWordListUI(engword);
			}
		}

		for (int i = scanData.wordList.size() - 1; i >= 0; i--) {
			if (!words.contains(scanData.wordList.get(i))) {
				mActivity.updateSubWordListUI(i);
			}
		}
	}

	// Function for initializing the renderer.
	private void initRendering() {
		// init the vert/inde buffers
		mROIVerts = ByteBuffer.allocateDirect(4 * ROIVertices.length);
		mROIVerts.order(ByteOrder.LITTLE_ENDIAN);
		updateROIVertByteBuffer();

		mROIIndices = ByteBuffer.allocateDirect(2 * ROIIndices.length);
		mROIIndices.order(ByteOrder.LITTLE_ENDIAN);
		for (short s : ROIIndices)
			mROIIndices.putShort(s);
		mROIIndices.rewind();

		mQuadVerts = ByteBuffer.allocateDirect(4 * quadVertices.length);
		mQuadVerts.order(ByteOrder.LITTLE_ENDIAN);
		for (float f : quadVertices)
			mQuadVerts.putFloat(f);
		mQuadVerts.rewind();

		mQuadIndices = ByteBuffer.allocateDirect(2 * quadIndices.length);
		mQuadIndices.order(ByteOrder.LITTLE_ENDIAN);
		for (short s : quadIndices)
			mQuadIndices.putShort(s);
		mQuadIndices.rewind();

		mRenderer = Renderer.getInstance();

		GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
				: 1.0f);

		shaderProgramID = AppUtils.createProgramFromShaderSrc(
				LineShaders.LINE_VERTEX_SHADER,
				LineShaders.LINE_FRAGMENT_SHADER);

		vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexPosition");
		mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"modelViewProjectionMatrix");

		lineOpacityHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"opacity");
		lineColorHandle = GLES20.glGetUniformLocation(shaderProgramID, "color");

	}

	private void updateROIVertByteBuffer() {
		mROIVerts.rewind();
		for (float f : ROIVertices)
			mROIVerts.putFloat(f);
		mROIVerts.rewind();
	}

	public void renderFrame() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		State state = mRenderer.begin();
		mRenderer.drawVideoBackground();

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// handle face culling, we need to detect if we are using reflection
		// to determine the direction of the culling
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON) {
			GLES20.glFrontFace(GLES20.GL_CW); // Front camera
		} else {
			GLES20.glFrontFace(GLES20.GL_CCW); // Back camera
		}

		// enable blending to support transparency
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);

		// did we find any trackables this frame?
		for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
			// get the trackable
			TrackableResult result = state.getTrackableResult(tIdx);

			Vec2F wordBoxSize = null;

			if (result.isOfType(WordResult.getClassType())) {
				WordResult wordResult = (WordResult) result;
				Word word = (Word) wordResult.getTrackable();

				Obb2D obb = wordResult.getObb();
				wordBoxSize = word.getSize();

				String wordU = word.getStringU();
				// ///////////////////////////////////////////
				if (wordU.length() > 2) {
					wordU = wordU.toLowerCase();
					tempList.add(wordU);
				} else {
					wordU = null;
				}

			} else {
				Log.d(LOGTAG, "Unexpected Detection : " + result.getType());
				continue;
			}

			Matrix44F mvMat44f = Tool.convertPose2GLMatrix(result.getPose());
			float[] mvMat = mvMat44f.getData();
			float[] mvpMat = new float[16];
			Matrix.translateM(mvMat, 0, 0, 0, 0);
			Matrix.scaleM(mvMat, 0, wordBoxSize.getData()[0] - TEXTBOX_PADDING,
					wordBoxSize.getData()[1] - TEXTBOX_PADDING, 1.0f);
			Matrix.multiplyMM(mvpMat, 0, vuforiaAppSession
					.getProjectionMatrix().getData(), 0, mvMat, 0);

			GLES20.glUseProgram(shaderProgramID);
			GLES20.glLineWidth(3.0f);
			GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
					false, 0, mQuadVerts);
			GLES20.glEnableVertexAttribArray(vertexHandle);
			GLES20.glUniform1f(lineOpacityHandle, 1.0f);
			GLES20.glUniform3f(lineColorHandle, 1.0f, 0.447f, 0.0f);
			GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMat, 0);
			GLES20.glDrawElements(GLES20.GL_LINES, NUM_QUAD_OBJECT_INDICES,
					GLES20.GL_UNSIGNED_SHORT, mQuadIndices);
			GLES20.glDisableVertexAttribArray(vertexHandle);
			GLES20.glLineWidth(1.0f);
			GLES20.glUseProgram(0);
		}

		// Draw the region of interest
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);

		drawRegionOfInterest(ROICenterX, ROICenterY, ROIWidth, ROIHeight);

		GLES20.glDisable(GLES20.GL_BLEND);

		mRenderer.end();
	}

	private void drawRegionOfInterest(float center_x, float center_y,
			float width, float height) {
		// assumption is that center_x, center_y, width and height are given
		// here in screen coordinates (screen pixels)
		float[] orthProj = new float[16];
		setOrthoMatrix(0.0f, (float) viewportSize_x, (float) viewportSize_y,
				0.0f, -1.0f, 1.0f, orthProj);

		// compute coordinates
		float minX = center_x - width / 2;
		float maxX = center_x + width / 2;
		float minY = center_y - height / 2;
		float maxY = center_y + height / 2;

		// Update vertex coordinates of ROI rectangle
		ROIVertices[0] = minX - viewportPosition_x;
		ROIVertices[1] = minY - viewportPosition_y;
		ROIVertices[2] = 0;

		ROIVertices[3] = maxX - viewportPosition_x;
		ROIVertices[4] = minY - viewportPosition_y;
		ROIVertices[5] = 0;

		ROIVertices[6] = maxX - viewportPosition_x;
		ROIVertices[7] = maxY - viewportPosition_y;
		ROIVertices[8] = 0;

		ROIVertices[9] = minX - viewportPosition_x;
		ROIVertices[10] = maxY - viewportPosition_y;
		ROIVertices[11] = 0;

		updateROIVertByteBuffer();

		GLES20.glUseProgram(shaderProgramID);
		GLES20.glLineWidth(5.0f);

		GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false,
				0, mROIVerts);
		GLES20.glEnableVertexAttribArray(vertexHandle);

		GLES20.glUniform1f(lineOpacityHandle, 1.0f); // 0.35f);
		GLES20.glUniform3f(lineColorHandle, 1.0f, 0.0f, 0.0f);// R,G,B
		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, orthProj, 0);

		// Then, we issue the render call
		GLES20.glDrawElements(GLES20.GL_LINES, NUM_QUAD_OBJECT_INDICES,
				GLES20.GL_UNSIGNED_SHORT, mROIIndices);

		// Disable the vertex array handle
		GLES20.glDisableVertexAttribArray(vertexHandle);

		// Restore default line width
		GLES20.glLineWidth(1.0f);

		// Unbind shader program
		GLES20.glUseProgram(0);
	}

	private void setOrthoMatrix(float nLeft, float nRight, float nBottom,
			float nTop, float nNear, float nFar, float[] _ROIOrthoProjMatrix) {
		for (int i = 0; i < 16; i++)
			_ROIOrthoProjMatrix[i] = 0.0f;

		_ROIOrthoProjMatrix[0] = 2.0f / (nRight - nLeft);
		_ROIOrthoProjMatrix[5] = 2.0f / (nTop - nBottom);
		_ROIOrthoProjMatrix[10] = 2.0f / (nNear - nFar);
		_ROIOrthoProjMatrix[12] = -(nRight + nLeft) / (nRight - nLeft);
		_ROIOrthoProjMatrix[13] = -(nTop + nBottom) / (nTop - nBottom);
		_ROIOrthoProjMatrix[14] = (nFar + nNear) / (nFar - nNear);
		_ROIOrthoProjMatrix[15] = 1.0f;

	}

	public void setROI(float center_x, float center_y, float width, float height) {
		ROICenterX = center_x;
		ROICenterY = center_y;
		ROIWidth = width;
		ROIHeight = height;
	}

	static String fromShortArray(short[] str) {
		StringBuilder result = new StringBuilder();
		for (short c : str)
			result.appendCodePoint(c);
		return result.toString();
	}

	public void setViewport(int vpX, int vpY, int vpSizeX, int vpSizeY) {
		viewportPosition_x = vpX;
		viewportPosition_y = vpY;
		viewportSize_x = vpSizeX;
		viewportSize_y = vpSizeY;
	}

	// ////////////////////////////////////////////view capture
	public void createBitmap() {
		int x = scanData.bitmapInfo.x;
		int y = scanData.bitmapInfo.y;
		int w = scanData.bitmapInfo.w;
		int h = scanData.bitmapInfo.h;
		Log.i("hari", "w:" + w + "-----h:" + h);

		int b[] = new int[(int) (w * h)];
		int bt[] = new int[(int) (w * h)];
		IntBuffer buffer = IntBuffer.wrap(b);
		buffer.position(0);
		GLES20.glReadPixels(x, y, w, h, GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE, buffer);
		for (int i = 0; i < h; i++) {
			// remember, that OpenGL bitmap is incompatible with Android bitmap
			// and so, some correction need.
			for (int j = 0; j < w; j++) {
				int pix = b[i * w + j];
				int pb = (pix >> 16) & 0xff;
				int pr = (pix << 16) & 0x00ff0000;
				int pix1 = (pix & 0xff00ff00) | pr | pb;
				bt[(h - i - 1) * w + j] = pix1;
			}
		}
		Bitmap inBitmap = null;
		if (inBitmap == null || !inBitmap.isMutable()
				|| inBitmap.getWidth() != w || inBitmap.getHeight() != h) {
			inBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		}
		// Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		inBitmap.copyPixelsFromBuffer(buffer);
		// return inBitmap ;
		// return Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
		inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
		scanData.bitmapInfo.bitmap = inBitmap;
	}
}
