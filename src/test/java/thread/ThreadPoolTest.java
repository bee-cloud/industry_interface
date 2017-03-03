package thread;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
/**
 * Created by weilei on 2016/11/22.
 */
public class ThreadPoolTest {

        public static void main(String[] args) throws InterruptedException {
        //创建一个可重用固定线程数的线程池
            ExecutorService pool = Executors.newFixedThreadPool(2);
           //创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
           Thread t1 = new MyThread();
         Thread t2 = new MyThread();
        Thread t3 = new MyThread();
         Thread t4 = new MyThread();
         Thread t5 = new MyThread();
          //将线程放入池中进行执行
          pool.execute(t1);
            Thread.currentThread().sleep(500);
          pool.execute(t2);
            Thread.currentThread().sleep(500);
           pool.execute(t3);
            Thread.currentThread().sleep(500);
           pool.execute(t4);
            Thread.currentThread().sleep(500);
          pool.execute(t5);


           //关闭线程池
          pool.shutdown();
           }


}
class MyThread extends Thread{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"正在执行。。。");
        System.out.println(this.getName());
        }
    }
