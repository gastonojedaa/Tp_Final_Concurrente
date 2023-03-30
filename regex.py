import re

regex = re.compile(r"T1 (.*?)T2 (.*?)T3 |T4 (.*?)T5 (.*?)T6 |T13 (.*?)T14 (.*?)T15 |T16 (.*?)T17 (.*?)T18 ")

with open("log.txt", "r") as file:
    content = file.read()
    #content = "T16 T1 T13 T16 T17 T18 T1 T2 T3 T4 T5 T6 T4 "
    new_content = ""
    running = True

    while running:
        #print("Content: ", content)
        new_content = re.sub(regex, '\g<1>\g<2>\g<3>\g<4>\g<5>\g<6>\g<7>\g<8>', content,1)
        #print("New content: ", new_content)
        if(content == new_content):
            running = False
        else:
            content = new_content
    print(new_content)
