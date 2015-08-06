#Branch second day

We use this branch to store the process of how to deal with image cache in Android system.

## Plan of the course

1. Instruction of different image cache level.
2. Memory cache image
3. Image messed up when showing in ListView, multiple threads, should add tag to ImageView to check.
4. Cache images with file, and load image view with Bitmap compressed


### Step 0 -> Step 1

1. 在UI线程中调用主线程；
2. 非UI线程中操作UI元素；
3. 使用Handler进行线程间交互；

### Step1 -> Step 2

1. 多线程操作导致图片读取错位；