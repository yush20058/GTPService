# GTPService

围棋GTP协议应用实例（一个自动对弈程序，命令行交互或命令行与GUI对弈）

一个IDEA项目，本项目是对之前GoAutomation项目的拓展，详细说明：http://www.caiyiwen.tech/article/34.html

GoAutomation项目地址：https://github.com/zhmgczh/GoAutomation

GoAutomation项目的详细说明：http://www.caiyiwen.tech/article/GoAutomation.html

根目录下各子目录说明：

（PhoenixGo） - 绝艺围棋GTP，此目录未保存到GitHub，若需使用可自行从https://github.com/Tencent/PhoenixGo/releases 下载

Leela - Leela围棋引擎GTP

LeelaZero - LeelaZero围棋引擎GTP

GNUGo - GNUGo围棋引擎GTP

src目录下各文件说明：

GTP.java - 围棋GTP引擎抽象类

LeelaGTP.java - Leela引擎GTP类

LeelaZGTP.java - LeelaZero引擎GTP类

PhoenixGoGTP.java - PhoenixGo引擎GTP类

GNUGo.java - GNUGo引擎GTP类

GTP_vs_GTP.java 两个GTP引擎对弈的转接器

GTP_vs_GUI.java 一个GTP引擎与GUI界面对弈的转接器（继承并发扬于GoAutomation项目）