delete
from message;

insert into message(id, text, tags, user_id)
values (1, 'first', 'my-tag', 1),
       (2, 'second', 'more', 1),
       (3, 'third', 'my-tag', 1),
       (4, 'fourth', 'another', 1);

alter sequence hibernate_sequence restart with 10;