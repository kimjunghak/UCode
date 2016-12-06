void main(){
    int i; int j; int k;
    int rem; int sum;
    int arr[10];

    i=2;

    test();

    while(i <= 500){
        sum = 0;
        k = i/2;
        j = 1;
        while(j<=k){
            rem =i%j;
            if(rem == 0){
                sum = sum + j;
            }
            ++j;
        }
        if(i == sum) write(i);
        ++i;
    }
}

void test(){
    int x; int y;
}
