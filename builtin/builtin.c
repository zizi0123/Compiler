#define bool _Bool


typedef unsigned size_t;

// Necessary functions in libc
int printf(const char *pattern, ...);
int sprintf(char *dest, const char *pattern, ...);
int scanf(const char *pattern, ...);
int sscanf(const char *src, const char *pattern, ...);
size_t strlen(const char *str);
int strcmp(const char *s1, const char *s2);
void *memcpy(void *dest, const void *src, size_t n);
void *malloc(size_t n);


void print(char *str){
    printf("%s",str);
}

void println(char *str) {
    printf("%s\n", str);
}

void printInt(int a){
    printf("%d",a);
}

void printlnInt(int a){
    printf("%d\n",a);
}

char *getString(){
    char *str = malloc(256);
    scanf("%s",str);
    return str;
}

int getInt(){
    int a;
    scanf("%d",&a);
    return a;
}

char *toString(int a){
    char *str = malloc(256);
    sprintf(str,"%d",a);
    return str;
}

int string_length(char *str){
    return strlen(str);
}

char *string_substring(char *str,int l,int r){
    char *a = malloc(256);
    for(int i = l;i<r;++i){
        a[i-l] = str[i];
    }
    a[r-l] = '\0';
    return a;
}

int string_parseInt(char *s){
    int x;
    sscanf(s,"%d",&x);
    return x;
}

int string_ord(char *s,int num){
    return s[num];
}

char *string_add(char *str1,char *str2){
    char *str = malloc(512);
    for(int i = 0; i < strlen(str1); ++i){
        str[i] = str1[i];
    }
    for(int i = 0; i < strlen(str2); ++i){
        str[i+strlen(str1)] = str2[i];
    }
    return str;
}

bool string_eq(char *str1,char *str2){
    return strcmp(str1,str2) == 0;
}

bool string_ne(char *str1,char *str2){
    return strcmp(str1,str2) != 0;
}

bool string_lt(char *str1,char *str2){
    return strcmp(str1,str2) < 0;
}

bool string_gt(char *str1,char *str2){
    return strcmp(str1,str2) > 0;
}

bool string_ge(char *str1,char *str2){
    return strcmp(str1,str2) >= 0;
}

bool string_le(char *str1,char *str2){
    return strcmp(str1,str2) <= 0;
}

int array_size(void *_this){
    return ((int*)_this)[-1];
}

void *_newPtrArray(int size){
    int *array  = malloc((size << 2) + 4);
    array[0] = size;
    return array + 1;
}

void *_newIntArray(int size){
    int *array  = malloc((size << 2) + 4);
    array[0] = size;
    return array + 1;
}

void *_newBoolArray(int size){
    int *array = malloc(size + 4);
    array[0] = size;
    return array + 1;
}












