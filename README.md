# Mental Load Helper app

## Description
Requested tool for helping keep track of misc. information, household TODOs, appointments, etc..

### Tech stack
Compose, coroutines, Supabase for remote db, PowerSync for local db & syncing from/to remote db. Also using various personal libraries for QoL - shared theme & componenets across other apps, misc. utilities, etc..


### TODO
Types of things to help manage
- [ ] Information
  - date created
  - text blob
  - tags
- [ ] One-off tasks
  - date created
  - due date
  - assigned person
  - tags
- [ ] Repeated tasks
  - date created
  - frequency
  - last completed
  - due date (some function of last completed and frequency, ex: sweep floors once a week, done sometime, dont need to do again for another week since last done)
  - "overdue" status based on ^
  - tags
- [ ] Notes dump
  - just a text blob
  - can select a chunk of text to extract into one of the above
- [ ] Users
  - Name
  - Avatar color
- [ ] Tags
  - Label
  - optional User Id
  - icon
  - color

Backend
- [ ] Setup backend in SB/PS
- [ ] Setup client schema for SB/PS
- [X] Setup auth for users

Client
- [X] Secrets file 
- [ ] Add repos, etc.
- [ ] Main screen: tabs for above
- [ ] Sorted by recency (vreated by default, options for completed or due-soon or overdue status for tasks)

Misc. UI/UX notes
- color task card according to assignee
- some sort of swipable stack would be nice
  - card headers peek for at a glance info
  - sort function by date (upcoming, due-by, etc.?)
  - tap to expand/retract
- card:
  - icon for content type (clock, cal, pencil, notepad)
  - icons for tags
  - browse material icons in-app? https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary
  - color cards for assigned user or by urgency/importance 
