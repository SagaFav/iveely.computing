import com.iveely.computing.node.Communicator;

/**
 * write  date: 2016年3月20日
 * author name: Iveely Liu
 * contact  me: sea11510@mail.ustc.edu.cn
 * description:
 */

/**
* @description 
* @author Iveely Liu
*/
/**
 * 
 */
public class Program {

    public static void main(String[] args) {
        int cnt = Communicator.getInstance().getUsedSlotCount();
        System.out.println(cnt);
    }
}
