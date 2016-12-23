/**
 * Created by allen on 2016/12/22.
 */
public enum SingletonExplain {

    LazyUnSafe,LazySafe,Hungry,DoubleCheck,StaticInner,Enum;


    public String getBinaryContent(String name) {
        String result = null;
        switch (this) {
            case LazyUnSafe:
                result=lazyUnSafe(name);
                break;

            case LazySafe:
                result=lazySafe(name);
                break;

            case StaticInner:
                result=staticInner(name);
                break;

            case Hungry:
                result=hungry(name);
                break;

            case DoubleCheck:
                result=doubleCheck(name);
                break;

            case Enum:
                result=enumString(name);
                break;

            default:
                break;
        }
        if(result==null||result.isEmpty())
            return null;
        // 返回包名 + 生成的单利代码
        return result;
    };


    private String lazyUnSafe(String className) {
        return
                "lazyUnsafe 的相关解释";
    }


    private String lazySafe(String className){
        return
                "lazysafe 的相关解释";
    }


    private String staticInner(String className){
        return
                "staticInner 的相关解释";
    }


    private String hungry(String className){
        return
                "hungry 的相关解释";
    }


    private String enumString(String className){
        return
                "enumString 的相关解释";
    }


    private String doubleCheck(String className) {
        return
                "doubleCheck 的相关解释";
    }
}
