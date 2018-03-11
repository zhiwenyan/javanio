import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import javax.xml.stream.events.Characters;

import org.junit.Test;

public class TestChannel {
	/**
	 * 通道Channel：用于源节点与目标节点的连接，在java NIO中负责缓冲区数据的传输。Channel
	 * 本身不存储数据，因此需要配合缓冲区的数据传输。
	 * 
	 * Java 为 Channel 接口提供的最主要实现类如下:
	 * •FileChannel:用于读取、写入、映射和操作文件的通道。
	 * •DatagramChannel:通过 UDP 读写网络中的数据通道。 
	 * •SocketChannel:通过 TCP 读写网络中的数据。
	 * •ServerSocketChannel:可以监听新进来的 TCP 连接，对每一个新进来 
	 * 的连接都会创建一个 SocketChannel。
	 * 
	 * 怎么获取通道？  
	 * 获取通道的一种方式是对支持通道的对象调用getChannel()方法。支持通道的类如下:
	 * 本地IO
	 * FileInputStream 
	 * FileOutputStream 
	 * RandomAccessFile 
	 * 网络IO
	 * DatagramSocket 
	 * Socket
	 * ServerSocket获取通道的其他方式是使用 Files 类的静态方法 newByteChannel() 
	 * 获 取字节通道。或者通过通道的静态方法 open() 打开并返回指定通道
	 *
	 *在JDK1.7中的NIO.2针对各个通道提供了静态的方法open().
	 *在JDK1.7中的NIO.2Files的工具类newByteChannel
	 *
	 *
	 *
	 * 
	 */
	@Test
	public void testChannel() {
		//CPU
		//DMA
		//Channel 完全独立的处理器，用于IO操作
		//利用通道完成文件的复制  
		FileInputStream fileInputStream=null;
		FileOutputStream fileOutputStream=null;
		//1、获取通道
		FileChannel inChannel=null;
		FileChannel outChannel=null;
		try {
			fileInputStream = new FileInputStream("1.jpg");
			fileOutputStream = new FileOutputStream("2.jpg");
			inChannel = fileInputStream.getChannel();
			outChannel = fileOutputStream.getChannel();
			//2、分配制定大小的缓冲区
			ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
			//3、将通道中的数据存入缓冲区中
			while(inChannel.read(byteBuffer)!=-1) {
				byteBuffer.flip(); //切换成读取数据的模式
				//将缓冲区的数据写入通道中
				outChannel.write(byteBuffer);
				byteBuffer.clear();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				//判断下为空 
				//TODO 
				outChannel.close();
				inChannel.close();
				fileOutputStream.close();
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Test
	public void testChannel2() throws IOException {
		//使用直接缓冲区完成文件的复制(内存映射文件)
		//1、获取通道
		FileChannel inChannel=FileChannel.open(Paths.get("1.jpg"), 
				StandardOpenOption.READ);
		FileChannel outChannel=FileChannel.open(Paths.get("3.jpg"), 
				//StandardOpenOption.CREATE_NEW StandardOpenOption.CREATE
				StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		//内存映射文件-->物理内存中
		MappedByteBuffer inMapbuf=
				inChannel.map(MapMode.READ_ONLY, 0,inChannel.size());
		
		MappedByteBuffer outMapbuf=
				inChannel.map(MapMode.READ_WRITE, 0,inChannel.size());
		//直接对缓冲区进行数据的读写操作 
		byte[] dst=new byte[inMapbuf.limit()];
		inMapbuf.get(dst);
		outMapbuf.put(dst);
		inChannel.close();
		outChannel.close();	
	}
	/**
	 * 四 通道之间数据传输  
	 * @throws IOException
	 */
	@Test
	public void testChannel3() throws IOException {
	
		FileChannel inChannel=FileChannel.open(Paths.get("1.jpg"), 
				StandardOpenOption.READ);
		FileChannel outChannel=FileChannel.open(Paths.get("3.jpg"), 
				//StandardOpenOption.CREATE_NEW StandardOpenOption.CREATE
				StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);
		//通道之间数据传输(也是直接缓冲区的方式)
		//inChannel.transferTo(0, inChannel.size(), outChannel);
		outChannel.transferFrom(inChannel, 0, inChannel.size());
		inChannel.close();
		outChannel.close();
	}

	/**
	 *  分散(Scatter)和聚集(Gather)
	 * @throws IOException 
	 *  
	 */
	@Test
	public void testChannel4() throws IOException {
		RandomAccessFile randomAccessFile=new RandomAccessFile("1.txt", "rw");
		//1、获取通道
		FileChannel fileChannel=randomAccessFile.getChannel();
		//2、分配指定大小的缓冲区  
		ByteBuffer buf1=ByteBuffer.allocate(100);
		ByteBuffer buf2=ByteBuffer.allocate(1024);
		//3、分散读取
		ByteBuffer[] byteBuffers= {buf1,buf2};
		fileChannel.read(byteBuffers);
		for (ByteBuffer byteBuffer : byteBuffers) {
			byteBuffer.flip();
		}
		System.out.println(new String(byteBuffers[0].array(),0,byteBuffers[0].limit()));
		System.out.println("--------------");
		System.out.println(new String(byteBuffers[1].array(),0,byteBuffers[1].limit()));
	
		//4、聚集写入  
		RandomAccessFile raf2=new RandomAccessFile("2.txt", "rw");
		FileChannel fileChannel2=raf2.getChannel();
		fileChannel2.write(byteBuffers);
		fileChannel2.close();
	}
	/**
	 * 字符集：Charest
	 * 编码：字符串转化成字节数组
	 * 解码：字节数组转化成字符串
	 */
	@Test
	public void testChannel5() {
	Map<String, Charset> map=Charset.availableCharsets();
	Set<java.util.Map.Entry<String,Charset>> set=map.entrySet();
	for (Entry<String, Charset> entry : set) {
		System.out.println(entry.getKey()+"="+entry.getValue());
	}
	}
	@Test
	public void testChannel6() throws CharacterCodingException {
	Charset charset=	Charset.forName("GBK");
	//获取编码器
	CharsetEncoder charsetEncodere=charset.newEncoder();
	//获取解码器
	CharsetDecoder charsetDecoder=charset.newDecoder();
	CharBuffer charBuffer=CharBuffer.allocate(1024);
	charBuffer.put("今天是个好天气");
	charBuffer.flip();
	//编码
	ByteBuffer byteBuffer=charsetEncodere.encode(charBuffer);
	for (int i = 0; i <byteBuffer.limit(); i++) {
	System.out.println(byteBuffer.get());
	}
	byteBuffer.flip();
	//解码
	CharBuffer charBuffer2=charsetDecoder.decode(byteBuffer);
	for (int i = 0; i < charBuffer2.limit(); i++) {
		System.out.print(charBuffer2.get());
		}
	}
}
