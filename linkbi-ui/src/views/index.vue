<template>
  <div>
    <iframe id="bdIframe"
            ref="myIframe"
            :src="iframeUrl"
            frameborder="no"
            style="width:100%; height:100%;"
            allowtransparency
            scrolling="no"></iframe>
  </div>
</template>

<script>
import elementResizeDetectorMaker from "element-resize-detector";
//import resizer from  "@/iframe-resizer/iframeResizer";
//const jwt = require("jsonwebtoken");this.baseConfig.baseUrl
//const mb_site_url = this.userConfig.metabaseUrl;//"http://localhost:3000";//8060/metabase";
/*
const mb_secret_key = "66e89a72eb2e22a40e4fad7dc49290fe8d060528e6999ef4df90f1467c9c6734";
const payload = {
    resource: {dashboard: 2},
    params: {},
    exp: Math.round(Date.now() / 1000) + (10 * 60) // 10 minute expiration
};
const token = jwt.sign(payload, mb_secret_key);
const iUrl = mb_site_url + "/embed/dashboard/" + token + "#refresh=60&bordered=false&titled=false";//#theme=night&
 */
//const iUrl = "/public/dashboard/eed88e73-a079-47ef-a86c-edf6cf2674b1" + "#refresh=60&bordered=false&titled=false";//#theme=night&
export default {
    name: 'Index',
    data() {
        return {
            iframeUrl: this.metabaseUrl,
        }

    },
    beforeCreate(){
      this.getConfigKey("sys.metabase.url").then(response => {
        this.iframeUrl = response.msg;
      });

    },
    mounted() {
        console.info(this.iframeUrl);
        //console.info(this.$store.state.tagsView.cachedViews);
        this.initIframeHeight();
        this.adaptIframeHeight();
        window.onresize = () => {
            const oIframe = document.getElementById('bdIframe');
            try {
                let iDoc = oIframe.contentDocument || oIframe.document || oIframe.contentWindow;
                let cHeight = 0;
                let sHeight = 0;
                if(!iDoc && typeof(iDoc) !== undefined)
                {
                    cHeight = Math.max(iDoc.body.clientHeight, iDoc.documentElement.clientHeight);
                    sHeight = Math.max(iDoc.body.scrollHeight, iDoc.documentElement.scrollHeight);
                }
                //console.info("cHeight=" + cHeight + ", sHeight=" + sHeight);
                let height = Math.max(cHeight, sHeight) < this.$root.mb_MaxHeight ? this.$root.mb_MaxHeight : Math.max(cHeight, sHeight);
                this.$root.mb_MaxHeight = this.$root.mb_MaxHeight < height ? height : this.$root.mb_MaxHeight;
                oIframe.style.height = height + 'px';
                //隐藏 Power By MetaBase
                if(!iDoc && typeof(iDoc) !== undefined)
                {
                    let footerDiv = iDoc.getElementsByClassName("EmbedFrame-footer");
                    if (footerDiv[0] !== undefined) {
                        footerDiv[0].style.display = 'none';
                    }
                }
            } catch (e) {
                console.error("窗口大小变化时，iframe高度自适应异常！" + e);
                oIframe.style.height = '1100px';
            }
        };
        try {
            const erd = elementResizeDetectorMaker();
            let sidebarWidth = null;
            // 监听左侧导航栏的宽度变化
            erd.listenTo(document.getElementsByClassName("sidebar-container"), (element) => {
                sidebarWidth = element.offsetWidth;
                this.$nextTick(() => {
                    let addHeight = 210;
                    // 导航栏收缩最小的时候，加大iframe的高度
                    if (sidebarWidth === 54) {
                        const oIframe = this.$refs.myIframe;
                        if (undefined !== oIframe) {
                            console.info(oIframe);
                            let oldHeight = oIframe.style.height;
                            if (undefined !== oldHeight && oldHeight !== '100%') {
                                oIframe.style.height = (Number(oldHeight.replace("px", ""))) + addHeight + 'px';
                            }
                        }
                    }
                    // 导航栏伸展到最大时，减少iframe的高度
                    if (sidebarWidth === 210) {
                        const oIframe = this.$refs.myIframe;
                        if (undefined !== oIframe) {
                            console.info(oIframe);
                            let oldHeight = oIframe.style.height;
                            if (undefined !== oldHeight && oldHeight !== '100%') {
                                oIframe.style.height = (Number(oldHeight.replace("px", ""))) - addHeight + 'px';
                            }
                        }
                    }
                })
            })
        } catch (e) {
            console.error("监听左侧导航栏宽度变化异常！" + e);
        }
    },
    methods: {
        initIframeHeight() {
            const oIframe = document.getElementById('bdIframe');
            try {
                let iDoc = oIframe.contentDocument || oIframe.document || oIframe.contentWindow;
                let cHeight = 0;
                let sHeight = 0;
                if(!iDoc && typeof(iDoc) !== undefined)
                {
                    cHeight = Math.max(iDoc.body.clientHeight, iDoc.documentElement.clientHeight);
                    sHeight = Math.max(iDoc.body.scrollHeight, iDoc.documentElement.scrollHeight);
                }
                //console.info("cHeight=" + cHeight + ", sHeight=" + sHeight);
                let height = Math.max(cHeight, sHeight) < this.$root.mb_MaxHeight ? this.$root.mb_MaxHeight : Math.max(cHeight, sHeight);
                this.$root.mb_MaxHeight = this.$root.mb_MaxHeight < height ? height : this.$root.mb_MaxHeight;
                oIframe.style.height = height + 'px';
                //隐藏 Power By MetaBase
                if(!iDoc && typeof(iDoc) !== undefined)
                {
                    let footerDiv = iDoc.getElementsByClassName("EmbedFrame-footer");
                    if (footerDiv[0] !== undefined) {
                        footerDiv[0].style.display = 'none';
                    }
                }
            } catch (e) {
                console.error("窗口大小变化时，iframe高度自适应异常！" + e);
                oIframe.style.height = '1100px';
            }
        },
        adaptIframeHeight() {
            const oIframe = document.getElementById('bdIframe');
            oIframe.onload = () => {
                try { // iframe高度自适应
                    let iDoc = oIframe.contentDocument || oIframe.document || oIframe.contentWindow;
                    let cHeight = 0;
                    let sHeight = 0;
                    if(!iDoc && typeof(iDoc) !== undefined)
                    {
                        cHeight = Math.max(iDoc.body.clientHeight, iDoc.documentElement.clientHeight);
                        sHeight = Math.max(iDoc.body.scrollHeight, iDoc.documentElement.scrollHeight);
                    }
                    let height = Math.max(cHeight, sHeight) < this.$root.mb_MaxHeight ? this.$root.mb_MaxHeight : Math.max(cHeight, sHeight);
                    this.$root.mb_MaxHeight = this.$root.mb_MaxHeight < height ? height : this.$root.mb_MaxHeight;
                    //console.info("cHeight=" + cHeight + ", sHeight=" + sHeight);
                    oIframe.style.height = height + 'px';
                    // 隐藏 Power By MetaBase
                    if(!iDoc && typeof(iDoc) !== undefined)
                    {
                        let footerDiv = iDoc.getElementsByClassName("EmbedFrame-footer");
                        if (footerDiv[0] !== undefined) {
                            footerDiv[0].style.display = 'none';
                        }
                    }
                } catch (e) {
                    console.error("iframe高度自适应异常！" + e);
                    oIframe.style.height = '1100px';
                }
            };
        }
    }
}
</script>
