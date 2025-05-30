# Проект "Сетевой чат"
## Описание проекта

Нужно разработать два приложения для обмена текстовыми сообщениями по сети с помощью консоли (терминала) между двумя и более пользователями.

Первое приложение - сервер чата, должно ожидать подключения пользователей.

Второе приложение - клиент чата, подключается к серверу чата и осуществляет доставку и получение новых сообщений.

Все сообщения должны записываться в file.log как на сервере, так и на клиентах. File.log должен дополняться при каждом запуске, а также при отправленном или полученном сообщении. Выход из чата должен быть осуществлен по команде exit.

## Требования к серверу

* Установка порта для подключения клиентов через файл настроек (например, settings.txt);
* Возможность подключиться к серверу в любой момент и присоединиться к чату;
* Отправка новых сообщений клиентам;
* Запись всех отправленных через сервер сообщений с указанием имени пользователя и времени отправки.

## Требования к клиенту

1. Выбор имени для участия в чате;
2. Прочитать настройки приложения из файла настроек - например, номер порта сервера;
3. Подключение к указанному в настройках серверу;
4. Для выхода из чата нужно набрать команду выхода - “/exit”;
5. Каждое сообщение участников должно записываться в текстовый файл - файл логирования. При каждом запуске приложения файл должен дополняться.

## Требования в реализации

* Сервер должен уметь одновременно ожидать новых пользователей и обрабатывать поступающие сообщения от пользователей;
* Использован сборщик пакетов gradle/maven;
* Код размещен на github;
* Код покрыт unit-тестами.
