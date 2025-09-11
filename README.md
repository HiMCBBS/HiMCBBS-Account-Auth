# HiMCBBS Account Auth
本项目是一个用于HiMCBBS账号认证的Spigot插件，支持通过HiMCBBS账号登录Minecraft服务器。
## 功能
- 支持MariaDB、MySQL、SQLite、JSON等多种user_id存储方式。
- 支持[AuthMeReloaded](https://github.com/AuthMe/AuthMeReloaded)（其实原定计划还有很多但是没写先鸽着吧）等登录插件。
- 易于开发者支持的API。
- 简单易懂的配置文件。
- Java 11及以上版本支持。
## 安装
1. 打开[Jenkins CI](https://ci.hiworldmc.com/job/HiMCBBS/job/HiMCBBS%20Account%20Auth/)界面下载最新构建版插件。
2. 将下载的插件放入服务器的`plugins`文件夹中。
3. 重启服务器。
4. 在`plugins/HiMCBBS-Account-Auth`目录下找到`config.yml`文件，按照注释进行数据库信息等配置。
5. 重启服务器使配置生效。
## 命令
- `/himcauth bind` - 绑定自己的HiMCBBS账号。
- `/himcauth unbind <玩家名称>` - 解绑对应玩家名称的HiMCBBS账号。
- `/himcauth reload` - 热重载插件配置文件。
## 权限
- `himcauth.bind` - 绑定自己HiMCBBS账号的权限。
- `himcauth.unbind` - 解绑玩家HiMCBBS账号的权限。
- `himcauth.reload` - 热重载插件配置文件的权限。
## API
- 咕咕咕……（没有文档，但是其实都写了javadoc，想写看看本插件是怎么写的，可以模仿）