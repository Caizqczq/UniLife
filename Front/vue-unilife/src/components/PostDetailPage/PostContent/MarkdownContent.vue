<template>
  <div class="markdown-body">
    <h1>这是 Markdown 标题</h1>
    <p>这是一段内容，这里是模拟 Markdown 的渲染文本。</p>
    <img src="@/assets/images/LogPage1.jpg" alt="预览图片" />
  </div>
</template>

<style scoped lang="scss">
.markdown-body {
  font-family: 'Georgia', serif;
  line-height: 1.6;

  h1 {
    font-size: 24px;
    margin-bottom: 12px;
  }

  p {
    margin: 10px 0;
  }

  img {
    max-width: 100%;
    margin: 10px 0;
    border-radius: 6px;
  }
}
</style>
