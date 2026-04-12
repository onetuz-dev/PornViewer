# Как установить и запустить PornViewer на школьном ноутбуке под управлением RedOS. Полное руководство.

Полный процесс делится на несколько ключевых шагов:

1. Скачать и установить PornViewer-2026w1.7.5-pv-linux(предоставляет код и скрипты для настройки)
   `curl -L -o PornViewer.zip https://github.com/anton-1488/PornViewer/archive/refs/heads/pv-linux.zip && unzip PornViewer.zip`
2. Настроить временное окружение и сессию на ноутбуке
3. Скачать и установить Java JDK 21 lunix-x64.tag.gz
   `curl -L -o jdk-21.jdk.tar.gz https://download.oracle.com/java/21/archive/jdk-21.0.2_linux-x64_bin.tar.gz`
4. Скачать и установить Maven 3.9.14.tar.gz
   `curl -L -o maven-3.9.14-bin.tar.gz https://dlcdn.apache.org/maven/maven-3/3.9.14/binaries/apache-maven-3.9.14-bin.tar.gz`
5. Собрать и запустить приложение:
   ```shell
   mvn clean package
   java -jar PornViewer.jar
   ```
6. Очистить сессию(вызвать ./cleanup.sh)

## Разберу как делать каждый шаг подробнее

*Интро
Первым делом необходимо раздать интернет с моего телефона на ноутбук.
Далее переключится на другой рабочий стол, чтобы изолировать сессию.

Для более удобного развертывания советую использовать скрипт setup.sh:
`chmod +x setup.sh && ./setup.sh`

### Шаг первый. Скачивание PornViewer-2026w1.7.5-pv-linux:

Необходимо скачать исходники PornViewer из ветки pv-linux:

```shell
curl -L -o PornViewer-linux.zip https://github.com/anton-1488/PornViewer/archive/refs/heads/pv-linux.zip
#Распаковка
unzip PornViewer-linux.zip && cd PornViewer-pv-linux
```

### Следующий шаг - настройка окружения.

Необходимо выполнить команду `chmod +x setup.sh && ./setup.sh`
Это настроит и автоматически выполнит следующие шаги.

### Далее, после успешной отработки скрипты соберем и запустим PornViewer, убедившись в корректности установок:

```shell
java -version
# should be:
#   java version "21.0.2" 2024-01-16 LTS
#   Java(TM) SE Runtime Environment (build 21.0.2+13-LTS-58)
#   Java HotSpot(TM) 64-Bit Server VM (build 21.0.2+13-LTS-58, mixed mode, sharing)
mvn -version # Должен показать версию 3.9.14 или хотя бы не упасть с ошибкой
```

После проверки, если все хорошо, соберем и запустим PornViewer:

```shell
mvn clean package
# После успешной сборки, артефакт появится в ./input-pv
# Запускаем
java -jar ./input-pv/PornViewer.jar
# Должно появится окно приложение, и минимум ошибок в логах.
# Миссия выполнена!
```

### Наигрвшись, необходимо обязательно очистить все что было скачано:

Для этого выполним скрипт cleanup.sh
`chmod +x cleanup.sh && ./cleanup.sh`

После этого можно закрыть сессию и выключить комп.

## Поздравляю, надеюсь, с успешной install-build-clean операцией.

(c) PlovDev 2026
(c) PornViewer 2026