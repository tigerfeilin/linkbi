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
export default {
    name: 'Index',
    data() {
        return {
          mb_MinHeight : 700,
          mb_HeightOffset: 80,
          iframeUrl: '',
        }

    },
    beforeCreate(){
      this.getConfigKey("sys.metabase.url").then(response => {
        this.iframeUrl = response.msg;
      });

    },
  mounted() {
    this.initIframeHeight();
    this.adaptIframeHeight();
    window.onresize = () => {
      const oIframe = this.$refs.myIframe;//document.getElementById('bdIframe');
      try {
        oIframe.style.height = document.documentElement.clientHeight - this.mb_HeightOffset + 'px';
      } catch (e) {
        console.error("窗口大小变化时，iframe高度自适应异常！" + e);
        oIframe.style.height = this.mb_MinHeight;
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
      const oIframe = this.$refs.myIframe;
      try {
        oIframe.style.height = document.documentElement.clientHeight - this.mb_HeightOffset + 'px';
      } catch (e) {
        console.error("窗口大小变化时，iframe高度自适应异常！" + e);
        oIframe.style.height = this.mb_MinHeight + 'px';
      }
    },
    adaptIframeHeight() {
      const oIframe = this.$refs.myIframe;
      oIframe.onload = () => {
        try { // iframe高度自适应
          oIframe.style.height = document.documentElement.clientHeight - this.mb_HeightOffset + 'px';
        } catch (e) {
          console.error("iframe高度自适应异常！" + e);
          oIframe.style.height = this.mb_MinHeight + 'px';
        }
      };
    }
  }
}
</script>
