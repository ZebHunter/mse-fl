# Грамматика языка (вариант 1)

## Команда:
- Рогачев Михаил - разработка документации и грамматики языка;
- Родионов Артем - разбор грамматики с помощью ANTLR4;
- Усманова Регина - работа с файлами и логика лексического разбора, настройка окружения;
- Садыков Руслан - CI и разработка тестов.

## 1. Краткая идея языка

Язык `Kocaml` — это небольшой императивный учебный язык с синтаксисом в стиле Kotlin/Java.
Он разрабатывается по варианту 1:

- объявление переменных;
- присваивание;
- арифметические операции;
- операции сравнения;
- объявление и вызов функций;
- условные операторы.

Грамматика реализована в ANTLR4 и хранится в файле `grammar/Kocaml.g4`.

## 2. Поддерживаемые конструкции

- **Объявление переменной**: `let x: Int = 10;`  
  Тип и инициализация опциональны:
  - `let x;`
  - `let x: Float;`
  - `let x = 1.5;`

- **Присваивание**: `x = x + 1;`

- **Функции**:
  - объявление: `fun add(a: Int, b: Int): Int { ... }`
  - вызов: `add(2, 3);`

- **Условный оператор**:
  - `if (x > 0) { ... }`
  - `if (x > 0) { ... } else { ... }`

- **Блок**: `{ statement* }`

## 3. Грамматика в форме Бэкуса-Наура (BNF)

```bnf
<program> ::= <statement_list>

<statement_list> ::= <statement> <statement_list> | ε

<statement> ::= <variable_declaration> ";"
              | <assignment> ";"
              | <function_declaration>
              | <if_statement>
              | <expression> ";"

<variable_declaration> ::= "let" <identifier> <opt_type_annotation> <opt_initializer>

<opt_type_annotation> ::= ":" <type_ref> | ε
<opt_initializer> ::= "=" <expression> | ε

<assignment> ::= <identifier> "=" <expression>

<function_declaration> ::= "fun" <identifier> "(" <opt_parameter_list> ")" <opt_return_type> <block>

<opt_parameter_list> ::= <parameter_list> | ε
<parameter_list> ::= <parameter> <parameter_list_tail>
<parameter_list_tail> ::= "," <parameter> <parameter_list_tail> | ε
<parameter> ::= <identifier> ":" <type_ref>

<opt_return_type> ::= ":" <type_ref> | ε

<if_statement> ::= "if" "(" <expression> ")" <block> <opt_else_block>
<opt_else_block> ::= "else" <block> | ε

<block> ::= "{" <statement_list> "}"

<expression> ::= <equality_expression>

<equality_expression> ::= <comparison_expression> <equality_tail>
<equality_tail> ::= ("==" | "!=") <comparison_expression> <equality_tail> | ε

<comparison_expression> ::= <additive_expression> <comparison_tail>
<comparison_tail> ::= (">" | ">=" | "<" | "<=") <additive_expression> <comparison_tail> | ε

<additive_expression> ::= <multiplicative_expression> <additive_tail>
<additive_tail> ::= ("+" | "-") <multiplicative_expression> <additive_tail> | ε

<multiplicative_expression> ::= <unary_expression> <multiplicative_tail>
<multiplicative_tail> ::= ("*" | "/" | "%") <unary_expression> <multiplicative_tail> | ε

<unary_expression> ::= "+" <unary_expression>
                     | "-" <unary_expression>
                     | <primary_expression>

<primary_expression> ::= <literal>
                       | <identifier>
                       | <function_call>
                       | "(" <expression> ")"

<function_call> ::= <identifier> "(" <opt_argument_list> ")"

<opt_argument_list> ::= <argument_list> | ε
<argument_list> ::= <expression> <argument_list_tail>
<argument_list_tail> ::= "," <expression> <argument_list_tail> | ε

<literal> ::= <int_literal>
            | <float_literal>
            | <string_literal>
            | <bool_literal>

<type_ref> ::= "Int" | "Float" | "String" | "Bool" | "Unit"
```
## 4. Типы данных

В грамматике предусмотрены следующие типы:

- `Int` — целые числа;
- `Float` — числа с плавающей точкой;
- `String` — строки в двойных кавычках;
- `Bool` — булев тип (`true` / `false`);
- `Unit` — тип для функций без возвращаемого значения.

## 5. Приоритет операций

Приоритет (от высокого к низкому):

1. **Скобки и первичные выражения**: литералы, идентификаторы, вызовы функций, `(expr)`;
2. **Унарные**: `+x`, `-x`;
3. **Мультипликативные**: `*`, `/`, `%`;
4. **Аддитивные**: `+`, `-`;
5. **Сравнение**: `>`, `>=`, `<`, `<=`;
6. **Равенство**: `==`, `!=`.

Все бинарные операции реализованы как левоассоциативные последовательности.

## 6. Лексическая структура

- **Идентификатор**: `[a-zA-Z_][a-zA-Z0-9_]*`;
- **Целый литерал**: `[0-9]+`;
- **Float-литерал**: `[0-9]+ '.' [0-9]+`;
- **Строковый литерал**: `"..."` с базовыми escape-последовательностями;
- **Комментарии**:
  - однострочные: `// ...`
  - блочные: `/* ... */`
- пробельные символы и комментарии пропускаются лексером.

## 7. Пример программы

```text
fun max(a: Int, b: Int): Int {
    let result: Int;
    if (a > b) {
        result = a;
    } else {
        result = b;
    }
    result;
}

let x: Int = 10;
let y: Int = 25;
let m: Int = max(x, y);
```


## 8. Как запускать и тестировать

В репозитории добавлен Kotlin CLI для лексического анализа и автотесты.

- Входной файл: любой текст программы на языке `Kocaml`.
- Выходной файл: тот же путь с суффиксом `.out`.
- Формат строки токена: `TYPE 'value' @ line:column`.
- При лексической ошибке в `.out` пишется:
  - `LEXER_ERROR`
  - строки с описанием ошибок и координатами.

Команды:

```bash
# 1) запуск лексера на конкретном файле
gradle run --args="src/test/resources/lexer/valid/sample.in"

# 2) запуск тестов
gradle test
```

Для GitLab CI используется `.gitlab-ci.yml`, где выполняется:

```bash
gradle --no-daemon clean test
```
