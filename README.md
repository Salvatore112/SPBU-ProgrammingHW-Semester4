# Домашние задания и контрольные по Практикуму на ЭВМ. 4-ый семестр.

# Задача 1. Стек Трайбера

На лекциях в осеннем семестре обсуждалось, как сделать работу со стеком
потокобезопасной. Первым делом вам следует просто реализовать те алгоритмы для
трёх основных операций (вставка, удаление, просмотр верхнего элемента), которые
мы разбирали вместе на занятии.

Далее следует изучить принцип работы оптимизации с элиминацией и дополнить ей
вашу реализацию стека.

Ожидается некоторое покрытие кода тестами, которые могли бы в той или иной
степени убедить проверяющего в корректности решения.
Наконец, необходимо провести численный эксперимент
- на случайных данных
- на специально подобранных данных,
призванный оценить эффективность оптимизации с элиминацией. Кстати говоря, что
по этому поводу говорят сами авторы оптимизации?

Комментарий. Язык программирования — любой со сборщиком мусора, где вы
сможете продуцировать ООП-код с продуманными и осмысленными
дизайн-решениями, а также где в стандартной библиотеке есть всякие вещи вроде
atomic или CAS. Обратите внимание, что для Kotlin существует специальный
фреймворк Lincheck, который позволяет делать более качественные тесты. Если вы
выбираете другой язык, задумайтесь о том, чем компенсировать отсутствие Lincheck’a.

[Решение](https://github.com/Salvatore112/SPBU-ProgrammingHW-Semester4/tree/Task1/Task1/LockFreeStack)

# Задача 2. Инструменты анализа кода

Возьмите любой проект с открытым исходным кодом на C/C++ без элементов OpenMP,
содержащий элементы параллельного программирования. Проанализируйте его
(насколько это возможно, всесторонне) с помощью инструментов Helgrind и
ThreadSanitizer или их аналогом.
Какие предупреждения выдаёт инструмент? Указывают ли они на реальную проблему
или хотя бы на ту, которая потенциально может возникнуть?
Далее необходимо внести в проект некоторую гонку данных (не совсем уж
искусственную) и обнаружить её с помощью инструмента.

Комментарий. Язык программирования — C/С++ в силу особенностей инструмента

[Решение](https://github.com/Salvatore112/mandelbrot)
