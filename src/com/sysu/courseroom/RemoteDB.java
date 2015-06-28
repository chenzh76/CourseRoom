package com.sysu.courseroom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.nfc.Tag;
import android.util.Log;

public class RemoteDB {
	private static String TAG = "MainActivity";
	private Socket socket;
	private String remoteIP = "172.18.32.125";
	private int remotePort = 2222;
	private static int BUFFER_SIZE = 1400;
	public RemoteDB() {
		socket = new Socket();
	}
	public Boolean connect() {
		if (socket.isConnected())
			return true;
		try {
			socket.connect(new InetSocketAddress(this.remoteIP, this.remotePort), 3000);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	public Boolean isConnected() {
		if (socket == null)
			return false;
		return socket.isConnected();
	}
	
	/** ��ȡ�γ̵�goodֵ
	 * @param id  �γ�id
	 * @return �ɹ����ؿγ̵�goodֵ��ʧ�ܷ���-1
	 */
	public int getGood(String id) {
		return getGoodOrBad(id, (byte)0);
	}
	
	/** ��ȡ�γ̵�badֵ
	 * @param id �γ�id
	 * @return �ɹ����ؿγ̵�badֵ��ʧ�ܷ���-1
	 */
	public int getBad(String id) {
		return getGoodOrBad(id, (byte)1);
	}
	
	private int getGoodOrBad(String id, byte type) {
		try {
			if (socket.isConnected() == false)
				return -1;
			OutputStream mOutputStream = socket.getOutputStream();
			InputStream mInputStream = socket.getInputStream();
			mOutputStream.write(type);
			byte idLength = (byte)id.getBytes("utf-8").length;
			mOutputStream.write(idLength);
			mOutputStream.write(id.getBytes("utf-8"));
			byte[] buffer = new byte[4];
			int acceptSize = mInputStream.read(buffer, 0, 4);
			if (acceptSize < 4) {
				acceptSize += mInputStream.read(buffer, acceptSize, 4 - acceptSize);
				if (acceptSize < 4) {
					return -1;
				}
			}
			int result = (buffer[0] >= 0 ? buffer[0] : 256 + buffer[0]) * (1 << 24) 
					+ (buffer[1] >= 0 ? buffer[1] : 256 + buffer[1]) * (1 << 16)
					+ (buffer[2] >= 0 ? buffer[2] : 256 + buffer[2]) * (1 << 8)
					+ (buffer[3] >= 0 ? buffer[3] : 256 + buffer[3]);
			return result;
		}catch (Exception e){
			return -1;
		}
	}
	
	/** ���ӿγ̵�goodֵ
	 * @param id �γ�id
	 * @return �ɹ�����true��ʧ�ܷ���false
	 */
	public Boolean increaseGood(String id) {
		return increaseGoodOrBad(id, (byte)2);
	}
	
	/** ���ӿγ̵�badֵ
	 * @param id �γ�id
	 * @return �ɹ�����true��ʧ�ܷ���false
	 */
	public Boolean increaseBad(String id) {
		return increaseGoodOrBad(id, (byte)3);
	}
	
	private Boolean increaseGoodOrBad(String id, byte type) {
		try {
			if (socket.isConnected() == false)
				return false;
			OutputStream mOutputStream = socket.getOutputStream();
			InputStream mInputStream = socket.getInputStream();
			mOutputStream.write(type);
			byte idLength = (byte)id.getBytes("utf-8").length;
			mOutputStream.write(idLength);
			mOutputStream.write(id.getBytes("utf-8"));
			return true;
		}catch (Exception e){
			return false;
		}
	}
	
	/** ��Զ�̷������������
	 * @param cmt Ҫ��ӵ�����
	 * @return    �Ƿ���ӳɹ�
	 */
	public Boolean addComment(Comment cmt) {
		try {
			if (socket.isConnected() == false)
				return false;
			OutputStream mOutputStream = socket.getOutputStream();
			InputStream mInputStream = socket.getInputStream();
			mOutputStream.write((byte)4);
			byte idLength = (byte)cmt.id.getBytes("utf-8").length;
			mOutputStream.write(idLength);
			mOutputStream.write(cmt.id.getBytes("utf-8"));
			
			byte timeLength = (byte)cmt.time.getBytes("utf-8").length;
			mOutputStream.write(timeLength);
			mOutputStream.write(cmt.time.getBytes("utf-8"));
			
			byte contentLength = (byte)cmt.content.getBytes("utf-8").length;
			mOutputStream.write(contentLength);
			mOutputStream.write(cmt.content.getBytes("utf-8"));
			return true;
		}catch (Exception e){
			return false;
		}
	}
	
	/** ��Զ�̷�������ѯ���ڻ����ĳ��ʱ��ĳ���γ̵�����
	 * @param id Ҫ��ѯ�Ŀγ�id
	 * @param id ʱ�䣬��ʽΪ "YY-MM-DD HH:MM"��ʽ
	 * @return  ����comment��ArrayList,��������null��û�н�����ؿյ�ArrayList
	 */
	public ArrayList<Comment> getComments(String id, String time) {
		ArrayList<Comment> result = new ArrayList<Comment>();
		try {
			if (socket.isConnected() == false)
				return null;
			OutputStream mOutputStream = socket.getOutputStream();
			InputStream mInputStream = socket.getInputStream();
			mOutputStream.write((byte)5);
			
			byte idLength = (byte)id.getBytes("utf-8").length;
			mOutputStream.write(idLength);
			mOutputStream.write(id.getBytes("utf-8"));
			
			byte timeLength = (byte)time.getBytes("utf-8").length;
			mOutputStream.write(timeLength);
			mOutputStream.write(time.getBytes("utf-8"));
			
			byte[] buffer = new byte[BUFFER_SIZE];
			int size = 0;
			while (size < 4) {
				size += mInputStream.read(buffer, size, 4 - size);
			}
			int commentNum = (buffer[0] >= 0 ? buffer[0] : 256 + buffer[0]) * (1 << 24) 
					+ (buffer[1] >= 0 ? buffer[1] : 256 + buffer[1]) * (1 << 16)
					+ (buffer[2] >= 0 ? buffer[2] : 256 + buffer[2]) * (1 << 8)
					+ (buffer[3] >= 0 ? buffer[3] : 256 + buffer[3]);
			Log.i(TAG, String.valueOf(commentNum));
			int idLen, timeLen, contentLen;
			while (commentNum-- > 0) {
				size = 0;
				while (size < 3) {
					size += mInputStream.read(buffer, size, 3 - size);
				}
				idLen = (int)buffer[0];
				timeLen = (int)buffer[1];
				contentLen = (int)buffer[2];
				Comment cmt = new Comment();
				size = 0;
				while (size < idLen) {
					size += mInputStream.read(buffer, size, idLen - size);
				}
				cmt.id = new String(buffer, 0, idLen);
				
				size = 0;
				while (size < timeLen) {
					size += mInputStream.read(buffer, size, timeLen - size);
				}
				cmt.time = new String(buffer, 0, timeLen);
				
				size = 0;
				while (size < contentLen) {
					size += mInputStream.read(buffer, size, contentLen - size);
				}
				cmt.content = new String(buffer, 0, contentLen);
				//Log.i(TAG, cmt.id + " " + cmt.time + " " + cmt.content);
				result.add(cmt);
			}
			return result;
		}catch (Exception e){
			return null;
		}
	}
}
