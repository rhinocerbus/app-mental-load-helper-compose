# Mental Load Helper app

## Description
Requested tool for helping keep track of misc. information, household TODOs, appointments, etc..

### Tech stack
Compose, coroutines, Supabase for remote db, PowerSync for local db & syncing from/to remote db. Also using various personal libraries for QoL - shared theme & componenets across other apps, misc. utilities, etc..

### TODO
- Data
- [ ] Setup backend in SB/PS
- [ ] Setup client schema for SB/PS
- [ ] Add repos, etc.
- [ ] Setup auth for users
- Types of things to help manage
- [ ] Users
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
- [ ] Tags
  - Label
  - optional User Id
- UI
- [ ] Main screen: tabs for above
- [ ] Sorted by recency (vreated by default, options for completed or due-soon or overdue status for tasks)
