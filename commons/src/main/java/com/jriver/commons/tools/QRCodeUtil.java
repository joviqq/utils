package com.jriver.commons.tools;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码生成工具类
 * 
 * @author river
 * @date 2015年3月12日 下午4:40:51
 * 
 */
public class QRCodeUtil
{
	private static final String	CHARSET		= "utf-8";
	private static final String	FORMAT_NAME	= "JPG";
	// 二维码尺寸
	private static final int	QRCODE_SIZE	= 300;
	// LOGO宽度
	private static final int	LOGO_WIDTH	= 60;
	// LOGO高度
	private static final int	LOGO_HEIGHT	= 60;

	/**
	 * 生成二维码
	 * 
	 * @param content
	 *            　二维码内容
	 * @param logoPath
	 *            　二维码中心的ＬＯＧＯ
	 * @param needCompressLogo
	 *            　是否对ＬＯＧＯ进行压缩
	 * @param Exception
	 * @return BufferedImage　二维码图片对象
	 * @throws
	 */
	public static BufferedImage createImage(String content, String logoPath,
			boolean needCompressLogo) throws Exception
	{
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
						: 0xFFFFFFFF);
			}
		}
		if (logoPath == null || "".equals(logoPath))
		{
			return image;
		}
		// 插入图片
		QRCodeUtil.insertImage(image, logoPath, needCompressLogo);
		return image;
	}

	/**
	 * 插入LOGO
	 * 
	 * @param source
	 *            二维码图片
	 * @param logoPath
	 *            LOGO图片地址
	 * @param needCompress
	 *            是否压缩
	 * @throws Exception
	 */
	private static void insertImage(BufferedImage source, String logoPath,
			boolean needCompressLogo) throws Exception
	{
		File file = new File(logoPath);
		if (!file.exists())
		{
			System.err.println("" + logoPath + "   该文件不存在！");
			return;
		}
		Image src = ImageIO.read(new File(logoPath));
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompressLogo)
		{ // 压缩LOGO
			if (width > LOGO_WIDTH)
			{
				width = LOGO_WIDTH;
			}
			if (height > LOGO_HEIGHT)
			{
				height = LOGO_HEIGHT;
			}
			Image image = src.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}

	/**
	 * 生成二维码(内嵌LOGO)
	 * 
	 * @param content
	 *            内容
	 * @param imgPath
	 *            LOGO地址
	 * @param output
	 *            输出流
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static void encode(String content, String imgPath,
			OutputStream output, boolean needCompress) throws Exception
	{
		BufferedImage image = QRCodeUtil.createImage(content, imgPath,
				needCompress);
		ImageIO.write(image, FORMAT_NAME, output);
	}

	/**
	 * 生成二维码
	 * 
	 * @param content
	 *            内容
	 * @param output
	 *            输出流
	 * @throws Exception
	 */
	public static void encode(String content, OutputStream output)
			throws Exception
	{
		QRCodeUtil.encode(content, null, output, false);
	}

}
