# Texas Poker

### Current Questions

Q: rule 跟example具体在放在什么级别的测试

Q: 只要在Business Rule出现的item都需要写具体的测试来验证码？

Q: 如何确定Example涉及到的state所归属的对象

Q: 目前SBE 写出来的example粒度有点大，比如要完成的满足第一个规则就需要写很多代码层的准备和design，如何做到incremental design

BizQ: 一轮中同时又多名玩家all in？如何设计此结算点

# assumption

- MVP选择的规则为地池限制德州扑克，因此不会出现all in情况

## Business Rule

- 德州扑克是多人参与的卡牌游戏，参与人数至少为2个玩家
    - e.g.
        - given: 玩家A，B
        - when: 开始游戏
        - then：每个玩家收到两张底牌，游戏轮次为Pre-flop，正在进行操作的玩家是A，等待进行操作的玩家是B，可进行的操作是`投注（bet）`，奖池为0

- 玩家分别进行四轮投注，每轮的投注将会进入`奖池(pot)`
    - e.g.玩家进行如下投注，最后奖池的`筹码(chips)`为**16**
- 玩家分别进行四轮投注，每轮的投注将会进入`奖池(pot)`
    - e.g.玩家进行如下投注，最后奖池的`筹码(chips)`为**16**

      | Player | Pre-flop | Flop | Turn | River |
      | ------ | -------- | ---- | ---- | ----- |
      | A      | 1        | 1    | 1    | 1     |
      | B      | 1        | 1    | 1    | 1     |
      | C      | 1        | 1    | 1    | 1     |
      | D      | 1        | 1    | 1    | 1     |
    
- 游戏中有三种角色，玩家以顺时针的方向进行投注
    - e.g. 场上有四名玩家，当进行第一轮投注开始时以庄家左边开始依次等待投注的玩家为小盲注(Small Blind)→大盲注(Big Blind)→普通玩家(Player)→庄家(Button)
- 大盲注的投注筹码为小盲注的两倍
    - e.g.
        - given: 游戏轮次为第一轮，已完成操作的玩家小盲注，奖池为2
        - when: 大盲注 bet
        - then：游戏轮次为第一轮，已完成操作的玩家大盲注，奖池为4
- 第一轮玩家不能`弃牌(Fold)`
- 每一轮投注时玩家可进行的操作为`跟注(Call) or 加注(Raise) or 过牌(Check) or 全押(all in) or 弃牌(Fold)`
    - e.g.
        - given: 游戏轮次为第二轮，正在进行操作的玩家是A，等待进行操作的玩家是BC，已完成操作的玩家D，这轮**没有**出现投注，可进行的操作是`跟注，过牌，弃牌`，退出玩家没有，奖池为7
        - when: A bet 1 chips
        - then：游戏轮次为第二轮，正在进行操作的玩家是B，等待进行操作的玩家是CD，已完成操作的玩家A，这轮**有**出现投注, 可进行的操作是`跟注，加注，弃牌`,退出玩家没有，奖池为8

        ---

        - given: 游戏轮次为第二轮，正在进行操作的玩家是A，等待进行操作的玩家是BC，已完成操作的玩家D，这轮**没有**出现投注, 可进行的操作是`跟注，过牌，弃牌`,退出玩家没有，奖池为7
        - when: A 过牌
        - then：游戏轮次为第二轮，正在进行操作的玩家是B，等待进行操作的玩家是CD，已完成操作的玩家A，可进行的操作是`跟注，加注，弃牌`,退出玩家没有，奖池为7

        ---

        - given: 游戏轮次为第三轮，正在进行操作的玩家是B，等待进行操作的玩家是CD，已完成操作的玩家A，这轮**有**出现投注, 可进行的操作是`跟注，加注，弃牌`，退出玩家没有,奖池为15
        - when: B 加注
        - then：游戏轮次为第三轮，正在进行操作的玩家是C，等待进行操作的玩家是DA，已完成操作的玩家B(Raise2)，可进行的操作是`跟注，加注，弃牌`,退出玩家没有，奖池为17

        ---

        - given: 游戏轮次为第三轮，正在进行操作的玩家是B，等待进行操作的玩家是CD，已完成操作的玩家A，这轮**有**出现投注, 可进行的操作是`跟注，加注，弃牌`,奖池为15
        - when: B 弃牌
        - then：游戏轮次为第三轮，正在进行操作的玩家是C，等待进行操作的玩家是DA，已完成操作的玩家B(Fold)，可进行的操作是`跟注，加注，弃牌`,退出玩家B，奖池为15

        ---

- 玩家选择`弃牌(Fold)` 时则不再参与后续轮次的投注并丢失已投入奖池的筹码
    - e.g. 游戏进行到第二轮，当B玩家投注时选择`弃牌` 那么此玩家不会继续参与第三轮游戏，并输掉已投入奖池的所有筹码
- 当玩家进行all in action的时间点作为游戏特殊的终结点进行结算，后续游戏不会包含all in的玩家
- 直到最后一轮投注结束，仍未退出的玩家会进入摊牌阶段
- 摊牌阶段，玩家通过手牌和公共牌选取最佳组合进行比较
- 如果新的一轮开始时场上只有一名玩家则此玩家成为赢家并赢取所有的奖池
    - e.g.玩家投注情况如下，则A玩家成为赢家, 获取的筹码为7

      | Player | Pre-flop | Flop | Turn | River |
      | ------ | -------- | ---- | ---- | ----- |
      | A      | 1        | 1    | 1    |       |
      | B      | 1        | fold |      |       |
      | C      | 1        | 1    | fold |       |
      | D      | 1        | fold |      |       |
    
- 握有最佳组合的五张牌的玩家，将赢得奖池里的所有筹码
- 如果最终是平局那手牌相同的玩家一起划分奖金