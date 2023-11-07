# Java_OOP
<h1 align="center">Привет, это наш общий репозиторий</a> 
<img src="https://github.com/blackcater/blackcater/raw/main/images/Hi.gif" height="32"/></h1>
<h3 align="center">Алексей Перевощиков и Владимир Бурусов из группы КН-201</h3>

<a><img src="https://readme-typing-svg.demolab.com?font=Sometype+Mono&pause=1000&center=true&random=false&width=435&lines=%D0%98%D0%B4%D0%B5%D1%82+%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%B0++%E2%80%A2+%E2%80%A2+%E2%80%A2" alt="Typing SVG" /></a>

Описание бота на уровне для стартового задания:
1. При вызове команды "/start" бот появляется и выдаёт пользователю сообщение о своих возможностях.
2. При вызове команды "/help" бот в любой момент снова выводит это сообщение.
3. При попытке обращения к боту по любому тексту, кроме ключевых слов (жанров литературы, доступных во всплывающей клавиатуре), бот отвечает пользователю собственным сообщением.
4. При вызове команды "/get" пользователь может получить случайную цитату от бота.
5. При вызове команды "/genre" пользователь получает сообщение с предложением выбрать жанр литературы из предложенных.
6. При выборе жанра пользователь получает рекомендации книг данного жанра.

<h3>Постановка задачи:</h3>

<h3 align="center">1️⃣Цель задачи</h3> Разработать телеграмм-бот на основе Java, который поддерживает ведение множества диалогов одновременно с разными пользователями и:

1) Позволяет пользователю вводить название прочитанной книги во время диалога с помощью команды /addbook, запоминает название книги и дату прочтения (дату добавления в список).

   - <small>(P.S. /addbook потому что интуитивно пользователю понятней вводить без изменения регистра)
   Дополнительно бот ведёт список книг, прочитанных пользователем, доступ к которому можно получить в любой момент с помощью команды «/getread».</small>

2) Позволяет выводить список прочитанных книг одного автора по команде /getbyauthor

3) Позволяет выводить список прочитанных книг в n-ном году по команде /getbyyear

4) Позволяет перейти в раздел для выбора жанра книг по команде /genre для последующей рекомендации книг в 1 из 4 жанров: детектив, романтика, научная фантастика, фэнтези, доступ к которой осуществляется по одноимённой жанру кнопке.

5) Позволяет пользователю исправить прочитанную книгу во время диалога с помощью команды /editbook.

6) Позволяет пользователю удалить прочитанную книгу во время диалога с помощью команды /removebook.

7) По команде пользователя /playpuzzle переходит в режим игры в загадки, где бот загадывает загадку пользователю и считывает все его ответы на неё: Если ответ правильный, то он выводит сообщение: "Верно! Следующая загадка: …". Если неверный: "Неверно! Попробуйте еще раз." Пользователь может запросить подсказку сообщением: "Дай подсказку". Если пользователь пишет "Следующая загадка" - бот записывает текущую загадку, как нерешенную в статистику пользователя и переходит к следующей загадке. При выходе из режима по команде /stoppuzzle показывается статистика ответов. Эти команды работают только в этом режиме. 
 

<h3 align="center">2️⃣ Формулировка</h3> 
Примеры диалога (в текстовом виде):
1) U:/haveread Зло, как оно есть
      Марве Бури
     2020  

   B: Книга 'Зло, как оно есть' от автора Марве Бури(год: 2020) успешно добавлена в список прочитанных!

2) U:/getbyauthor Марве Бури
   B:Книги автора Марве Бури:
     Зло, как оно есть

3) U:/getbyyear 2020
   B:Книги 2020 года:
     Зло, как оно есть

4) U: /editbook 2
   B: Введите название книги
   U: Самая лучшая книга
   B: Введите автора книги
   U: Самый лучший автор
   B: Введите год написания книги
   U: 2021
   B: книга обновлена

5)U:/getread 
  B:Прочитанные книги: 
    1. Рекрут 
    2. 11.22.64, Кинг, 2021 
    3. Собака баскервилей
   U:/removebook 2
   B:Книга “11.22.64”, Кинг, 2021 была удалена 

6) U:/playpuzzle 
   B:Добро пожаловать в игру в загадки! Начнем.  
     Загадка: *Загадка*
   U:*Неверный ответ*
   
   B:Неверно! Попробуйте еще раз.
   U:Дай подсказку
   
   B:*подсказка*
   U:*Верный ответ*
   
   B:Верно
     Следующая загадка: *загадка*

   U:/stoppuzzle    
   B:Режим головоломки завершен. 
     Правильных ответов: 1   
     Неправильных ответов: 0   
     Процент правильных ответов: 100.0% 


<h3 align="center">3️⃣ Планы</h3> 
Планируется реализация навыка загадывания загадок ботом, а также обработка ответов пользователя, для сбора общей статистики ответов, несколько уровней сложности, предложение перейти на новый уровень, при ответе верно на все загадки без ошибок на данном уровне.

Игра угадай книгу: бот даёт описание книги, а пользователь должен угадать её название, возможны подсказки автора и года издания.

Голосование за цитату недели/месяца среди пользователей бота и вывод этой цитаты по запросу

Помощь в ведении литературного дневника в виде выдачи краткой информации о запрашивает произведении: главные герои, сюжет, автор, год издания, обозначение к какому из 7 основных сюжетов/тропов сводится данный
