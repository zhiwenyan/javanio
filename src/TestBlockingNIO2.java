import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

/**
 * 
 * 使用NIO完成网络通信的三个核心：
 * 1、通道（Channel）：负责连接
 * 2、缓冲区（Buffer）：负责数据的存取
 * 3、选择器（Selector）：是SelectableChannel的多路复用器。用于监控SelectableChannel
 * 的IO状况。
 * 
 *
 */
public class TestBlockingNIO2 {
	//客户端
	@Test
	public void client() throws IOException {
	//1、获取通道
	SocketChannel socketChannel=
			SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
	FileChannel fileChannel=FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
	//2、分配指定大小的缓冲区
	ByteBuffer buffer=ByteBuffer.allocate(1024);
	//3、读取本地文件，并发送到服务器  
	while(fileChannel.read(buffer)!=-1) {
		buffer.flip();
		socketChannel.write(buffer);
		buffer.clear();
	}
	socketChannel.shutdownOutput();
	//接受服务端的反馈 
	int len=0;
	while((len=socketChannel.read(buffer))!=-1) {
		System.out.println(new String(buffer.array(),0,len));
		buffer.clear();
	}
	//4、关闭通道
	fileChannel.close();
	socketChannel.close();
	}
	//服务端
	@Test
	public void server() throws IOException {
		//1、获取通道
	ServerSocketChannel serverSocketChannel=	ServerSocketChannel.open();
	FileChannel outChannel=FileChannel.open(Paths.get("7.jpg"), StandardOpenOption.WRITE,StandardOpenOption.CREATE_NEW);

	//2、绑定连接
	serverSocketChannel.bind(new InetSocketAddress(9898));
	//3、获取客户端连接的通道
	SocketChannel socketChannel=serverSocketChannel.accept();
	//4、分配指定大小的缓冲区
	ByteBuffer buffer=ByteBuffer.allocate(1024);
	//5、接受客户都的需求，并保存在本地
	while(socketChannel.read(buffer)!=-1) {
		buffer.flip();
		outChannel.write(buffer);
		buffer.clear();
	}
	//发送反馈的客户端
	buffer.put("服务端接收数据成功".getBytes());
	buffer.flip();
	socketChannel.write(buffer);
	
	//6、关闭通道
	socketChannel.close();
	outChannel.close();
	serverSocketChannel.close();
		
	}
}
