import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class Test1 {

    public static void main(String[] args) {
        String salt = "9180140447e52cfca1ef59cb07c941c2";
        String password = "a1234567";
        String oldPassword = "aafe23d3dc3e44c9e17a3aeaece8b74f";
        String newPassword =
                new SimpleHash("md5", password, ByteSource.Util.bytes(salt + salt), 2).toHex();
        System.out.println(newPassword);
    }

}
