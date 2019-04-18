import com.kuaidao.common.util.MD5Util;

public class Test1 {

    public static void main(String[] args) {
        String password = "a26de31dad2fd8afd4d98c05d13727b8";
        String stringToMd5 = MD5Util
                .StringToMd5(MD5Util.StringToMd5("a123456" + "a14f968615394e6ea9ba35afd74a8574"));
        System.out.println(password);
        System.out.println(stringToMd5);

    }

}
