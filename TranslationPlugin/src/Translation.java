/**
 * Created by allen on 2016/11/28.
 */
public class Translation {

    /**
     * msg : SUCCESS
     * status_code : 0
     * data : {"cn_definition":{"pos":"","defn":"adj. 是的\nadv. 是, 是的\nv. 是"},"definition":" adj. 是的\nadv. 是, 是的\nv. 是","url":"https://www.shanbay.com/bdc/mobile/preview/word?word=yes","en_definition":{"pos":"n","defn":"an affirmative"},"pronunciation":"jes","audio":"http://media.shanbay.com/audio/us/yes.mp3"}
     */

    private String msg;
    private int status_code;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * cn_definition : {"pos":"","defn":"adj. 是的\nadv. 是, 是的\nv. 是"}
         * definition :  adj. 是的
         adv. 是, 是的
         v. 是
         * url : https://www.shanbay.com/bdc/mobile/preview/word?word=yes
         * en_definition : {"pos":"n","defn":"an affirmative"}
         * pronunciation : jes
         * audio : http://media.shanbay.com/audio/us/yes.mp3
         */

        private CnDefinitionBean cn_definition;
        private String definition;
        private String url;
        private EnDefinitionBean en_definition;
        private String pronunciation;
        private String audio;

        public CnDefinitionBean getCn_definition() {
            return cn_definition;
        }

        public void setCn_definition(CnDefinitionBean cn_definition) {
            this.cn_definition = cn_definition;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public EnDefinitionBean getEn_definition() {
            return en_definition;
        }

        public void setEn_definition(EnDefinitionBean en_definition) {
            this.en_definition = en_definition;
        }

        public String getPronunciation() {
            return pronunciation;
        }

        public void setPronunciation(String pronunciation) {
            this.pronunciation = pronunciation;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }

        public static class CnDefinitionBean {
            /**
             * pos :
             * defn : adj. 是的
             adv. 是, 是的
             v. 是
             */

            private String pos;
            private String defn;

            public String getPos() {
                return pos;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public String getDefn() {
                return defn;
            }

            public void setDefn(String defn) {
                this.defn = defn;
            }
        }

        public static class EnDefinitionBean {
            /**
             * pos : n
             * defn : an affirmative
             */

            private String pos;
            private String defn;

            public String getPos() {
                return pos;
            }

            public void setPos(String pos) {
                this.pos = pos;
            }

            public String getDefn() {
                return defn;
            }

            public void setDefn(String defn) {
                this.defn = defn;
            }
        }
    }

    @Override
    public String toString() {
        String results = null;

        if(msg.equals("SUCCESS")){
            results = "释义: " + getData().getDefinition() + "\n音标：" + getData().getPronunciation()
                    + "\n网络查询：" + getData().getUrl() + "\n 语音播报：" + getData().getAudio()
                    + "\n英文释义：" + getData().getEn_definition().getDefn();
        }

        if(results == null)
        {
            results= "你选的内容 vino 翻译不了！";
        }

        return results;
    }
}
