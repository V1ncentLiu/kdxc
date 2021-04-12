public class Test1 {

    public static void main(String[] args) {
        String regex = "^,*|,*$";

        String str =",abc,def,123,";

        String str1 = str.replaceAll(regex, "");

        System.out.println(str1);
    }

}
