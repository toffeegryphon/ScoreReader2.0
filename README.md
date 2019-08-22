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

## Users
1. Scroll SeekBar to adjust height of each line (segment).
2. Scroll and bookmark lines.
3. Click done.

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
displaySize)`
> `document` | PDF File

> `recyclerView` | RecyclerView for the document (and subsequently
> segments)

> `displaySize` | Device width and height

### DocumentAdapter
Takes in the PdfRenderer of the PDF File and the target display size of
the page, using the `page.xml` layout.

`new DocumentAdapter(renderer, displaySize)` creates a RecyclerView
Adapter of renderer, with each page of displaySize.x pixels wide and
displaySize.y pixels tall. ScrollView enables the resulting bitmap to be
larger than screen size.

### SegmentBuilder
For bookmarking, generating Bitmap Segments (Lines), adjusting Segment
Heights

#### Constructor
`SegmentBuilder(DocumentRecycler documentRecycler, ViewGroup container)`

> `documentRecycler` | Source DocumentRecycler

> `container` | Parent layout for the `segment_builder.xml` to be
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

## ChangeLog
### 21/08/2019
1. Improved `calculate()`
2. Added `SegmentAdapter`
3. Added `finish()`
4. Renamed `getPosition()` to `bookmarkPosition`
5. Added `segment_outline.xml`
6. Added Functionality: Segment outline, and SeekBar to change
   `segmentHeight`
---
7. Abstracted portions of DocumentRecycler to SegmentBuilder, such that
  Document Recycler deals with generating Bitmap RecyclerView from PDF
  File only.
8. Abstracted Buttons/SeekBar to SegmentBuilder. (TODO: Create API
   endpoints for users to implement their own versions)
   
#### 22/08/2019
1. Added inputs for beats per minute (BPM), beats per bar (BPB), and
   bars per line/segment (BPL)

## To Do
1. Decide between file saving implementations - a config file vs saving
   the whole bitmap array.
2. Implement chosen save method.
3. Implement advanced bar indication.
4. Auto scrolling.
5. File picker