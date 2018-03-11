
import java.nio.ByteBuffer;

import org.junit.Test;

/***
 * 
 * 
Buffer 就像一个数组，可以保存多个相同类型的数据。根
据数据类型不同(boolean 除外) ，有以下 Buffer 常用子类:
ByteBuffer
CharBuffer
ShortBuffer 
IntBuffer
LongBuffer 
FloatBuffer 
DoubleBuffer
上述 Buffer 类 他们都采用相似的方法进行管理数据，只是各自 管理的数据类型不同而已。
都是通过如下方法获取一个 Buffer
对象:
static XxxBuffer allocate(int capacity) :
创建一个容量为 capacity 的 XxxBuffer 对象
二、缓冲区存储数据的两个核心方法
put():存入数据到缓冲区
get():获取缓冲区中的数据  
三、缓冲区中的四个核心属性：
容量 (capacity) :表示 Buffer 最大数据容量，缓冲区容量不能为负，并且创
建后不能更改。
限制 (limit):第一个不应该读取或写入的数据的索引，即位于 limit 后的数据 不可读写。缓冲区的限制不能为负，并且不能大于其容量。
位置 (position):下一个要读取或写入的数据的索引。缓冲区的位置不能为 负，并且不能大于其限制
标记 (mark)与重置 (reset):标记是一个索引，通过 Buffer 中的 mark() 方法 指定 Buffer 中一个特定的 position，之后可以通过调用 reset() 方法恢复到这 个 position.
标记、位置、限制、容量遵守以下不变式:0<=mark<=position<=limit<=capacity
 五：直接缓冲区与非直接缓冲区
非直接缓冲区：通过allocate（）方法分配缓冲区，将缓冲区建立在JVM的内存中。
直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中。
*
*/
public class BufferrTest {

	public static void main(String[] args) {
	String string="abc";
	//分配一个指定大小的缓冲区
	ByteBuffer buffer=ByteBuffer.allocate(1024);
	System.out.println(buffer.position());
	System.out.println(buffer.limit());
	System.out.println(buffer.capacity());
	//利用put()存入数据
	buffer.put(string.getBytes());
	System.out.println("******************");
	System.out.println(buffer.position());
	System.out.println(buffer.limit());
	System.out.println(buffer.capacity());
	//切换成读取数据的模式
	buffer.flip();
	System.out.println("******************");
	System.out.println(buffer.position());
	System.out.println(buffer.limit());
	System.out.println(buffer.capacity());
	//利用get()读取缓冲区的数据  
	byte[] bytes=new byte[buffer.limit()];
	buffer.get(bytes);
	System.out.println("******************");
	System.out.println(new String(bytes, 0, bytes.length));
	System.out.println(buffer.position());
	System.out.println(buffer.limit());
	System.out.println(buffer.capacity());
	//rewind()可重复读数据
	buffer.rewind();
	System.out.println("******************");
	System.out.println(buffer.position());
	System.out.println(buffer.limit());
	System.out.println(buffer.capacity());
	//clear():清空缓冲区,但是缓冲区的数据依然存在，但是处于“被遗忘”的状态。
	buffer.clear();
	System.out.println("******************");
	System.out.println(buffer.position());
	System.out.println(buffer.limit());
	System.out.println(buffer.capacity());
	System.out.println(buffer.get());
	
	ByteBuffer buffer1=ByteBuffer.allocate(1024);
	String string1="abcde";
	buffer1.put(string1.getBytes());
	buffer1.flip();
	byte[] bytes1=new byte[buffer1.limit()];
	buffer1.get(bytes1,0,2);
	System.out.println("******************");
	System.out.println(new String(bytes1, 0, 2));
	System.out.println(buffer1.position());
	buffer1.mark();
	buffer1.get(bytes1,2,2);
	
	System.out.println(new String(bytes1, 2, 2));
	System.out.println(buffer1.position());
	buffer1.reset();
	System.out.println(buffer1.position());
	//判断缓冲区数据中还有没有操作的数据
	if(buffer1.hasRemaining()) {
		//缓冲区剩下可以操作的数据
		System.out.println(buffer1.remaining());
	}
}
	@Test
	public void test3() {
	ByteBuffer byteBuffer=ByteBuffer.allocateDirect(1024);
	System.out.println(byteBuffer.isDirect());
	}
	
}
