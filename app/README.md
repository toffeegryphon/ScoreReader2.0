# ScoreReader 2.0

## Problem
Sheet music is now often carried on the phone/tablet (media devices) for
ease of access. However, it is not as easy to read on media devices due
to the smaller screen. If the sheets are zoomed in, the musician will
need to scroll the page way more often, making it less efficient and
more troublesome.

## Idea
An application to flip the page automatically, every so often to keep
the music flowing.

## Potential Development
- Some sort of OCR to determine each bar/line
- Get the tempo of the music and flip accordingly (based on number of
  bars per line/page)
  
## Implementation
### DocumentRecycler
Abstraction of the PDF Document RecyclerView package (View, ViewHolder,
Adapter). Takes in the file source, view xml and display size. Meant for
setting up bookmarks and cutting the document up into segments.

#### Constructor 
`DocumentRecycler(File document, RecyclerView recyclerView, Point
displaySize, ViewGroup outlineContainer)`
> `document` | PDF File

> `recyclerView` | RecyclerView for the document (and subsequently
> segments)

> `displaySize` | Device width and height

> `outlineContainer` | Parent layout for the segmentOutline to be
> inflated into

#### Methods
`bookmarkPosition()` 
> Gets the current offset position, and adds to
bookmarks list. Calls `calculate()` and adds generated segment Bitmap to
segments list.

`calculate()` 
> Based on bookmarked position and page height, generates
and returns a Bitmap of the bookmarked segment. (TODO: add variable
`segmentHeight`)

`finish()`
> Concludes bookmarking and changes the Adapter to the SegmentAdapter.
> Shows the new segmented document.

#### Values
`segmentHeight`
> Height of each segment in px.

### DocumentAdapter
Takes in the PdfRenderer of the PDF File and the target display size of
the page, using the `page.xml` layout.

`new DocumentAdapter(renderer, displaySize)` creates a RecyclerView
Adapter of renderer, with each page of displaySize.x pixels wide and
displaySize.y pixels tall. ScrollView enables the resulting bitmap to be
larger than screen size.

## ChangeLog
### 21/08/2019
- Improved `calculate()`
- Added `SegmentAdapter`
- Added `finish()`
- Renamed `getPosition()` to `bookmarkPosition`
- Added `segment_outline.xml`
- Added Functionality: Segment outline, and SeekBar to change
  `segmentHeight`