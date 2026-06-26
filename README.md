# 基于有状态的课堂点名系统 v1.0

## 系统简介

基于有状态的课堂点名系统是一个面向高校课堂教学的辅助工具，旨在解决传统随机点名中"有的同学被点名多次、有的同学从未被点到"的不公平问题。

系统通过记录每位学生的历史被点名次数和回答问题次数，采用**加权随机算法**，确保在一个学期的教学过程中，每位学生获得均等的被点名机会。当连续多人无法回答问题时，系统自动启动**救场机制**，从历史回答率较高的同学中选取，避免课堂僵局。

## 功能概览

| 模块 | 功能 |
|------|------|
| 学生管理 | 单个/批量添加、Excel导入导出、搜索过滤、请假/销假管理 |
| 课堂点名 | 加权随机点名、救场机制、题目太难提示、继续提问模式 |
| 数据统计 | 汇总卡片（总数/总点名/总答出/成功率）、排名表、频次分布 |

## 技术栈

- **语言**：Java 8+
- **GUI框架**：Java Swing（JFrame / JTabbedPane / JTable）
- **数据持久化**：Java对象序列化（ObjectOutputStream / ObjectInputStream），文件存储（students.dat / records.dat）
- **第三方库**：Apache POI 5.3.0（Excel .xlsx 读写）
- **架构模式**：MVC三层架构 + DAO数据访问模式 + 策略模式

## 项目结构

```
课堂点名系统/
├── src/rollcall/
│   ├── model/
│   │   ├── Student.java            # 学生实体（序列化、权重计算）
│   │   └── RollCallRecord.java     # 点名记录实体
│   ├── dao/
│   │   └── StudentDAO.java         # 数据访问层（文件持久化）
│   ├── service/
│   │   ├── RollCallService.java    # 核心点名服务（状态管理）
│   │   └── StatisticsService.java  # 统计服务（汇总/频次/排名）
│   ├── util/
│   │   ├── WeightedRandom.java     # 加权随机算法
│   │   └── ExcelUtil.java          # Apache POI Excel读写工具
│   └── view/
│       ├── MainFrame.java          # 主窗口（JTabbedPane）
│       ├── StudentPanel.java       # 学生管理面板
│       ├── RollCallPanel.java      # 课堂点名面板
│       └── StatisticsPanel.java    # 数据统计面板
├── lib/                            # 第三方依赖
│   ├── poi-5.3.0.jar
│   ├── poi-ooxml-5.3.0.jar
│   ├── poi-ooxml-lite-5.3.0.jar
│   ├── xmlbeans-5.2.1.jar
│   ├── commons-compress-1.26.2.jar
│   ├── commons-collections4-4.4.jar
│   ├── commons-io-2.16.1.jar
│   └── log4j-api-2.21.1.jar
├── bin/
│   └── 课堂点名系统.jar             # 可运行JAR包
├── doc/
│   ├── 学生导入模板.csv             # Excel导入模板
│   ├── 学生数据.csv                 # 示例数据
│   └── 系统使用说明书.docx          # 详细操作指南
└── README.md
```

## 本地运行

### 方式一：直接运行JAR包（推荐）

```bash
java -jar bin/课堂点名系统.jar
```

### 方式二：从源码编译运行

```bash
# 1. 编译（需将lib下所有jar加入classpath）
javac -encoding UTF-8 -cp "lib/*" -d out src/rollcall/**/*.java

# 2. 运行
java -cp "out:lib/*" rollcall.view.MainFrame
```

### 环境要求

- JDK 8 或更高版本
- 无需安装数据库或其他额外软件

## 数据文件说明

系统在运行目录下自动创建两个数据文件：

| 文件 | 内容 | 格式 |
|------|------|------|
| `students.dat` | 学生信息（学号、姓名、班级、点名/答出次数、请假状态） | Java序列化 |
| `records.dat` | 点名记录（学号、姓名、课程、是否答出、时间） | Java序列化 |

- 退出程序时自动保存，启动时自动加载
- 备份数据：直接复制这两个文件即可
- 重置数据：删除这两个文件即可恢复到初始状态

## 点名算法说明

### 加权随机算法

每位学生的点名权重 = `100 / (被点名次数 + 1)`，被点名次数越少，权重越高，被选中的概率越大。

采用**累积权重法**：计算所有学生权重之和 → 生成 [0, 总和) 随机数 → 从第一个学生开始累加权重，首次超过随机数时选中该学生。

### 救场机制

当同一问题连续3位学生未答出时，系统切换为救场模式：从历史回答率最高的学生中随机选取（回答率需达到最高回答率的80%以上）。在救场模式下若连续3人也未答出，弹出提示"题目太难，建议老师讲解"。

## License

本项目为课程设计作业，仅供学习交流使用。
