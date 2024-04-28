#!/usr/bin/env bash

## 工具脚本

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

### 程序jar包
APP_JAR_NAME="$DIR/jk-api.jar"

### 程序名 无影响
APP_NAME="jk-api"

### 程序类全名
APP_MAIN_CLASS="cn.rh.flash.api.ApiApplication"

### 程序的api库文件夹
LIB="$DIR/lib"

### springboot 配置文件激活
CONF_ACTIVE="prod"

### 日志文件夹
LOG_DIR="$DIR/logs"
if [ ! -d $LOG_DIR ]; then
  mkdir -p $LOG_DIR
fi

### java虚拟机参数
Xmx=5g
Xms=1g
Xmn=3g
Xss=1m

JAVA_OPTS="java -XX:+UseG1GC -Xmx${Xmx} -Xms${Xms} -Xmn${Xmn} -Xss${Xss} -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -XX:+PrintGCDetails  -Xloggc:${LOG_DIR}/${APP_NAME}-gc.log  -XX:+HeapDumpOnOutOfMemoryError -XX:+AlwaysPreTouch -XX:HeapDumpPath=${LOG_DIR}/ -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dspring.config.location=config/ -Dspring.profiles.active=${CONF_ACTIVE}"

pid=0

###检查pid是否存在
check() {
  pidinfo=$(jps -l | grep ${APP_MAIN_CLASS})
  if [ -n "$pidinfo" ]; then
    pid=$(echo $pidinfo | awk '{print $1}')
  else
    pid=0
  fi
}

###启动应用程序
start() {
  #  check
  #
  #  if [ $pid -ne 0 ]; then
  #    echo -e "\033[31m warn: ${APP_NAME} already started! (pid=${pid}) \033[0m"
  #  else

  echo -n "启动 ${APP_NAME}..."

  ###获取到lib下的所有jar包
  jarlib=".:${APP_JAR_NAME}:"

  for jarname in $(ls ${LIB}); do
    jarlib="${jarlib}${LIB}/${jarname}:"
  done

  ###启动
  nohup $JAVA_OPTS -classpath $jarlib ${APP_MAIN_CLASS} > ${LOG_DIR}/${APP_NAME}.log 2>&1 &
  ##检查是否启动
  check
  if [ $pid -ne 0 ]; then
    echo -e "\033[32m (进程pid=${pid}) [成功] \033[0m"
  else
    echo -e "\033[31m [失败] \033[0m"
  fi
  #  fi
}

### 停止应用程序
stop() {
  check

  if [ $pid -ne 0 ]; then
    echo -n "停止 $APP_NAME... (pid=${pid}) "
    kill -9 $pid
    if [ $? -eq 0 ]; then
      echo -e "\033[32m [成功] \033[0m"
    else
      echo -e "\033[31m [失败] \033[0m"
    fi
  else
    echo -e "\033[31m 警告: ${APP_NAME} 没有运行 \033[0m"
  fi
}

### 重启
restart() {
  stop
  start
}

### 检查是否在运行
status() {
  check

  if [ $pid -ne 0 ]; then
    echo -e "\033[32m $APP_NAME 已经在运行中! (pid=$pid) \033[0m"
  else
    echo -e "\033[31m $APP_NAME 未在运行! \033[0m"
  fi
}

log() {
  tail -f ${LOG_DIR}/${APP_NAME}.log
}

update() {
  front_dirname="dist"
  update_filename="update.tar.gz"
  update_front_filename="update-front.tar.gz"
  if [ ! -f $DIR/update/${update_filename} ];then
    echo "无后端更新包!!"
    exit 1
  else
    tar -xvf $DIR/update/${update_filename} -C $DIR/lib/
    rm -rf $DIR/$APP_JAR_NAME
    mv $DIR/lib/$APP_JAR_NAME $DIR/
    echo "更新后端成功, 请启动应用"
  fi

  if [ ! -f $DIR/update/${update_front_filename} ];then
    echo "无前端更新包!!"
    exit 1
  else
    if [ -d $DIR/$front_dirname ];then
      rm -rf $DIR/$front_dirname
    fi
    tar -xvf $DIR/update/${update_front_filename} -C $DIR/
    echo "更新前端成功"
  fi

}

# ## 状态信息
# info() {

# }

####
case "$1" in
'start')
  start
  ;;
'stop')
  stop
  ;;
'restart')
  restart
  ;;
'status')
  status
  ;;
'log')
  log
  ;;
'update')
  update
  ;;
*)
  echo -e "使用方法: ./service.sh {start|stop|restart|status|update|log}\n"
  printf "%-10s->  %s\n" "start" "启用应用"
  printf "%-10s->  %s\n" "stop" "停止应用"
  printf "%-10s->  %s\n" "restart" "重启应用"
  printf "%-10s->  %s\n" "status" "应用状态"
  printf "%-10s->  %s\n" "log" "应用当前运行日志"
  printf "%-10s->  %s\n" "update" "更新应用"
  exit 1
  ;;
esac
exit 0
