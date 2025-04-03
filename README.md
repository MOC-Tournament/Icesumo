# 冰上相扑

在冰上相扑项目中，运动员需要使用下界合金锄，将对方击退到下方水池。

项目完整规则：<相关规则>

## 命令进度

状态标注：

U:Uncompleted 未完成

P:Partly Completed 部分完成

C:Completed 完成，但需要测试

T:Tested 完成，初步测试通过

F:Final 完全完成

- [C] 检录与播报
- [C] 启动（晋级赛）
- [U] 启动（决赛）
- [C] 循环判定
- [C] 成功判定
- [C] 失败判定
- [C] 中断、急停、复位

### 各届特别命令

- [ ] 倒计时启动时的额外命令 `moc:ice_sumo/specific/pre_start`
- [ ] 比赛开始时的额外命令 `moc:ice_sumo/specific/on_start`
- [ ] 比赛结束、中断时的额外命令 `moc:ice_sumo/specific/on_stop`