# CommonAdapter

扩展自[tianzhijiexian/CommonAdapter](https://github.com/tianzhijiexian/CommonAdapter)

## Usage

```
repositories{
  maven { url "http://maven.mjtown.cn/" }
}

dependencies{
  compile "name.zeno:common-adapter:$version"
}
```
[version](https://github.com/zenochan/CommonAdapter/releases)

## FEATURES
- [x] LoadAdapterWrapper: 对分页列表UI的封装
- [x] SectionedRcvAdapter: Section 分组显示适配

## CHANGE LOG

#### 2.0.1810150
- 迁移至 AndroidX, 不再支持 support 系列
- 移除 `CommonMDRcvAdapter`

#### 1.0.1

- 添加 `CommonMDRcvAdapter`
