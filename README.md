# RxPhotograph
响应式的图片选择器，选择图片功能和Activity解耦，不在依赖onActivityResult方法。   

# How to use  

To use this library your minSdkVersion must be >= 21.  

```
implementation 'com.anima:CListView:1.0.6'
```

# Example
  

    new RxPhotograph(this).request().subscribe(new rx.functions.Action1<Photograph>() {
            @Override
            public void call(Photograph photograph) {
                ImageView imageView = findViewById(R.id.mainAct_imageView);
                imageView.setImageBitmap(photograph.getBitmap());
            }
        });

	... ...

	public class Photograph {
    	private String name;
    	private Bitmap bitmap;
    	private String filePath;
	}
